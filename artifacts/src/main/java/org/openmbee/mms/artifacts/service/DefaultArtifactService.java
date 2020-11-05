package org.openmbee.mms.artifacts.service;

import org.openmbee.mms.artifacts.storage.ArtifactStorage;
import org.openmbee.mms.artifacts.ArtifactConstants;
import org.openmbee.mms.artifacts.objects.ArtifactResponse;
import org.openmbee.mms.core.dao.ProjectDAO;
import org.openmbee.mms.core.exceptions.BadRequestException;
import org.openmbee.mms.core.exceptions.ConflictException;
import org.openmbee.mms.core.exceptions.NotFoundException;
import org.openmbee.mms.core.objects.ElementsRequest;
import org.openmbee.mms.core.objects.ElementsResponse;
import org.openmbee.mms.core.services.NodeService;
import org.openmbee.mms.crud.services.ServiceFactory;
import org.openmbee.mms.data.domains.global.Project;
import org.openmbee.mms.artifacts.json.ArtifactJson;
import org.openmbee.mms.json.ElementJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class DefaultArtifactService implements ArtifactService {

    private ArtifactStorage artifactStorage;
    private ServiceFactory serviceFactory;
    private ProjectDAO projectRepository;

    @Autowired
    public void setArtifactStorage(ArtifactStorage artifactStorage) {
        this.artifactStorage = artifactStorage;
    }

    @Autowired
    public void setServiceFactory(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Autowired
    public void setProjectRepository(ProjectDAO projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public ArtifactResponse get(String projectId, String refId, String id, Map<String, String> params) {
        NodeService nodeService = getNodeService(projectId);
        ElementJson elementJson = getElement(nodeService, projectId, refId, id, params);

        ArtifactJson artifact = getExistingArtifact(ArtifactJson.getArtifacts(elementJson), params);
        byte[] data = artifactStorage.get(artifact.getLocation(), elementJson, artifact.getMimeType());
        ArtifactResponse response = new ArtifactResponse();
        response.setData(data);
        response.setExtension(artifact.getExtension());
        response.setMimeType(artifact.getMimeType());
        return response;
    }

    @Override
    public ElementsResponse createOrUpdate(String projectId, String refId, String id, MultipartFile file, String user, Map<String, String> params) {
        NodeService nodeService = getNodeService(projectId);
        ElementJson elementJson;
        try {
            elementJson = getElement(nodeService, projectId, refId, id, params);
        } catch(NotFoundException ex){
            elementJson = new ElementJson();
            elementJson.setProjectId(projectId);
            elementJson.setId(id);
            elementJson.setInRefIds(Arrays.asList(refId));
        }

        byte[] fileContents;
        try {
            fileContents = file.getBytes();
        } catch (IOException e) {
            throw new BadRequestException("Could not get contents of multipart file.");
        }

        String mimeType = getMimeTypeOfFile(file);
        String fileExtension = getFileExtension(file);
        String artifactLocation = artifactStorage.store(fileContents, elementJson, mimeType);

        elementJson = attachOrUpdateArtifact(elementJson, artifactLocation, fileExtension, mimeType, "internal");
        ElementsRequest elementsRequest = new ElementsRequest();
        elementsRequest.setElements(Arrays.asList(elementJson));
        return nodeService.createOrUpdate(projectId, refId, elementsRequest, params, user);
    }

    @Override
    public ElementsResponse disassociate(String projectId, String refId, String id, String user, Map<String, String> params) {
        NodeService nodeService = getNodeService(projectId);
        ElementJson elementJson = getElement(nodeService, projectId, refId, id, params);

        List<ArtifactJson> artifacts = ArtifactJson.getArtifacts(elementJson);
        ArtifactJson artifact = getExistingArtifact(artifacts, params);
        artifacts.remove(artifact);
        ArtifactJson.setArtifacts(elementJson, artifacts);
        ElementsRequest elementsRequest = new ElementsRequest();
        elementsRequest.setElements(Arrays.asList(elementJson));
        return nodeService.createOrUpdate(projectId, refId, elementsRequest, params, user);
    }

    private ElementJson getElement(NodeService nodeService, String projectId, String refId, String id, Map<String, String> params) {

        ElementsResponse elementsResponse = nodeService.read(projectId, refId, id, params);
        if(elementsResponse.getElements() == null || elementsResponse.getElements().isEmpty()) {
            throw new NotFoundException("Element not found");
        } else if(elementsResponse.getElements().size() > 1) {
            throw new ConflictException("Multiple elements found with id " + id);
        } else {
            return elementsResponse.getElements().get(0);
        }
    }

    private ElementJson attachOrUpdateArtifact(ElementJson elementJson, String artifactLocation, String fileExtension, String mimeType, String type) {

        List<ArtifactJson> artifacts = ArtifactJson.getArtifacts(elementJson);
        ArtifactJson artifact;
        try {
            artifact = getExistingArtifact(artifacts, mimeType, null);
        } catch(NotFoundException ex) {
            artifact = new ArtifactJson();
            artifacts.add(artifact);
        }

        artifact.setLocation(artifactLocation);
        artifact.setExtension(fileExtension);
        artifact.setMimeType(mimeType);
        artifact.setLocationType(type);

        ArtifactJson.setArtifacts(elementJson, artifacts);
        return elementJson;
    }

    private ArtifactJson getExistingArtifact(List<ArtifactJson> artifacts, Map<String, String> params) {
        return getExistingArtifact(artifacts, params.get(ArtifactConstants.MIMETYPE_PARAM), params.get(ArtifactConstants.EXTENSION_PARAM));
    }

    private ArtifactJson getExistingArtifact(List<ArtifactJson> artifacts, String mimeType, String extension) {
        if(mimeType == null && extension == null) {
            throw new BadRequestException("Missing mimetype or extension");
        }
        //Element representation is unique by mimeType and extension
        Optional<ArtifactJson> existing = artifacts.stream().filter(v -> {
            return (mimeType != null && mimeType.equals(v.getMimeType())) || (extension != null && extension.equals(v.getExtension()));
        }).findFirst();
        if(existing.isPresent()) {
            return existing.get();
        }
        throw new NotFoundException("Artifact not found");
    }

    private String getFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if(originalFilename != null) {
            int inx = originalFilename.lastIndexOf('.');
            if(inx > 0) {
                return originalFilename.substring(inx + 1);
            }
        }
        return null;
    }

    private String getMimeTypeOfFile(MultipartFile file) {
        return file.getContentType();
    }

    private NodeService getNodeService(String projectId) {
        return serviceFactory.getNodeService(getProjectType(projectId));
    }

    private String getProjectType(String projectId) {
        return getProject(projectId).getProjectType();
    }

    private Project getProject(String projectId) {
        Optional<Project> p = projectRepository.findByProjectId(projectId);
        if (p.isPresent()) {
            return p.get();
        }
        throw new NotFoundException("project not found");
    }
}
