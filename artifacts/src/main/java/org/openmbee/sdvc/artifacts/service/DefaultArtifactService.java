package org.openmbee.sdvc.artifacts.service;

import org.openmbee.sdvc.artifacts.objects.ArtifactResponse;
import org.openmbee.sdvc.artifacts.storage.ArtifactStorage;
import org.openmbee.sdvc.core.dao.ProjectDAO;
import org.openmbee.sdvc.core.exceptions.BadRequestException;
import org.openmbee.sdvc.core.exceptions.ConflictException;
import org.openmbee.sdvc.core.exceptions.NotFoundException;
import org.openmbee.sdvc.core.objects.ElementsRequest;
import org.openmbee.sdvc.core.objects.ElementsResponse;
import org.openmbee.sdvc.core.services.NodeService;
import org.openmbee.sdvc.crud.services.ServiceFactory;
import org.openmbee.sdvc.data.domains.global.Project;
import org.openmbee.sdvc.artifacts.json.ArtifactJson;
import org.openmbee.sdvc.json.ElementJson;
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
        if(elementJson == null){
            return null;
        }

        ArtifactJson artifact = getExistingArtifact(ArtifactJson.getArtifacts(elementJson), params);
        if(artifact != null) {
            byte[] data = artifactStorage.get(artifact.getLocation(), elementJson, artifact.getMimeType());
            ArtifactResponse response = new ArtifactResponse();
            response.setData(data);
            response.setExtension(artifact.getExtension());
            response.setMimeType(artifact.getMimeType());
            return response;
        }
        return null;
    }

    @Override
    public ElementsResponse createOrUpdate(String projectId, String refId, String id, MultipartFile file, String user, Map<String, String> params) {
        NodeService nodeService = getNodeService(projectId);
        ElementJson elementJson = getElement(nodeService, projectId, refId, id, params);

        if(elementJson == null) {
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
        nodeService.createOrUpdate(projectId, refId, elementsRequest, params, user);

        ElementsResponse response = new ElementsResponse();
        response.setElements(Arrays.asList(elementJson));
        return response;
    }

    @Override
    public ElementsResponse disassociate(String projectId, String refId, String id, Map<String, String> params) {
        ElementsResponse response = new ElementsResponse();
        NodeService nodeService = getNodeService(projectId);
        ElementJson elementJson = getElement(nodeService, projectId, refId, id, params);
        if(elementJson == null) {
            throw new NotFoundException("Element not found");
        }

        List<ArtifactJson> artifacts = ArtifactJson.getArtifacts(elementJson);
        if(artifacts == null) {
            throw new NotFoundException("Artifact not found");
        }

        ArtifactJson artifact = getExistingArtifact(artifacts, params);
        if(artifact != null) {
            artifacts.remove(artifact);
            ArtifactJson.setArtifacts(elementJson, artifacts);
            response.setElements(Arrays.asList(elementJson));
        }

        return response;
    }

    private ElementJson getElement(NodeService nodeService, String projectId, String refId, String id, Map<String, String> params) {

        ElementsResponse elementsResponse = nodeService.read(projectId, refId, id, params);
        if(elementsResponse.getElements() == null || elementsResponse.getElements().isEmpty()) {
            return null;
        } else if(elementsResponse.getElements().size() > 1) {
            throw new ConflictException("Multiple elements found with id " + id);
        } else {
            return elementsResponse.getElements().get(0);
        }
    }

    private ElementJson attachOrUpdateArtifact(ElementJson elementJson, String artifactLocation, String fileExtension, String mimeType, String type) {

        List<ArtifactJson> artifacts = ArtifactJson.getArtifacts(elementJson);
        if(artifacts == null) {
            artifacts = new ArrayList<>(1);
        }

        ArtifactJson artifact = getExistingArtifact(artifacts, mimeType, null);

        if(artifact == null) {
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
        return getExistingArtifact(artifacts, params.get("mimeType"), params.get("extension"));
    }

    private ArtifactJson getExistingArtifact(List<ArtifactJson> artifacts, String mimeType, String extension) {
        if(mimeType == null && extension == null) {
            return null;
        }
        if(artifacts == null) {
            return null;
        }
        //Element representation is unique by mimeType and extension
        Optional<ArtifactJson> existing = artifacts.stream().filter(v -> {
            return (mimeType != null && mimeType.equals(v.getMimeType())) || (extension != null && extension.equals(v.getExtension()));
        }).findFirst();
        if(existing.isPresent()) {
            return existing.get();
        }
        return null;
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
