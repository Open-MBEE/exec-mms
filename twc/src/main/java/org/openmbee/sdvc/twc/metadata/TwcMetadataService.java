package org.openmbee.sdvc.twc.metadata;

import org.openmbee.sdvc.data.domains.global.Metadata;
import org.openmbee.sdvc.data.domains.global.Project;
import org.openmbee.sdvc.rdb.repositories.MetadataRepository;
import org.openmbee.sdvc.twc.constants.TwcConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TwcMetadataService {


    private MetadataRepository metadataRepository;

    @Autowired
    public void setMetadataRepository(MetadataRepository metadataRepository) {
        this.metadataRepository = metadataRepository;
    }

    public void updateTwcMetadata(Project project, TwcMetadata metadata) {
        updateTwcMetadata(project, TwcConstants.HOST_KEY, metadata.getHost());
        updateTwcMetadata(project, TwcConstants.WORKSPACE_ID_KEY, metadata.getWorkspaceId());
        updateTwcMetadata(project, TwcConstants.RESOURCE_ID_KEY, metadata.getResourceId());
    }

    private void updateTwcMetadata(Project project, String fieldKey, String value) {

        Metadata metadata = null;
        List<Metadata> metadataList = project.getMetadata().stream()
            .filter(v -> fieldKey.equals(v.getKey())).collect(Collectors.toList());

        if(metadataList.size() > 0) {
            metadata = metadataList.get(0);
        }
        //All of the TWC metadata fields should be unique. If more than one exists, remove the extras.
        if(metadataList.size() > 1) {
            metadataRepository.deleteAll(metadataList.subList(1, metadataList.size()));
        }

        //If null and exists, delete
        if(value == null) {
            if(metadata != null) {
                metadataRepository.delete(metadata);
            }
        }
        else {
            //If not null and exists, update
            if(metadata != null) {
                metadata.setValue(value);
                metadataRepository.save(metadata);
            }
            //If not null and does not exist, create
            else {
                metadata = new Metadata();
                metadata.setProject(project);
                metadata.setKey(fieldKey);
                metadata.setValue(value);
                metadataRepository.save(metadata);
            }
        }
    }

    public TwcMetadata getTwcMetadata(Project project) {
        TwcMetadata twcMetadata = new TwcMetadata();
        twcMetadata.setHost(getMetadataField(project, TwcConstants.HOST_KEY));
        twcMetadata.setWorkspaceId(getMetadataField(project, TwcConstants.WORKSPACE_ID_KEY));
        twcMetadata.setResourceId(getMetadataField(project, TwcConstants.RESOURCE_ID_KEY));
        return twcMetadata;
    }

    private String getMetadataField(Project project, String key) {
        if(project.getMetadata() == null)
            return null;

        Optional<String> value = project.getMetadata().stream()
            .filter(v -> key.equals(v.getKey())).map(Metadata::getValue).findFirst();
        return value.orElse(null);
    }
}
