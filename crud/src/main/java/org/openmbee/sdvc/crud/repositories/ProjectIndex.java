package org.openmbee.sdvc.crud.repositories;

public interface ProjectIndex {

    void create(String projectId);

    void create(String projectId, String projectType);

    void delete(String projectId);

}
