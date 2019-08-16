package org.openmbee.sdvc.core.services;

public interface ProjectIndex {

    void create(String projectId);

    void create(String projectId, String projectType);

    void delete(String projectId);

}
