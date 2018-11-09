package org.openmbee.sdvc.crud.controllers;

import java.util.HashMap;

public class BaseJson extends HashMap<String, Object> {
    public String getId() {
        return (String) this.get("id");
    }

    public BaseJson setId(String id) {
        this.put("id", id);
        return this;
    }

    public String getName() {
        return (String) this.get("name");
    }

    public BaseJson setName(String name) {
        this.put("name", name);
        return this;
    }

    public String getElasticId() {
        return (String) this.get("_elasticId");
    }

    public BaseJson setElasticId(String elasticId) {
        this.put("_elasticId", elasticId);
        return this;
    }

    public String getProjectId() {
        return (String) this.get("_projectId");
    }

    public BaseJson setProjectId(String projectId) {
        this.put("_projectId", projectId);
        return this;
    }

    public String getRefId() {
        return (String) this.get("_refId");
    }

    public BaseJson setRefId(String refId) {
        this.put("_refId", refId);
        return this;
    }

    public String getModifier() {
        return (String) this.get("_modifier");
    }

    public BaseJson setModifier(String modifier) {
        this.put("_modifier", modifier);
        return this;
    }

    public String getCreator() {
        return (String) this.get("_creator");
    }

    public BaseJson setCreator(String creator) {
        this.put("_creator", creator);
        return this;
    }

    public String getCreated() {
        return (String) this.get("_created");
    }

    public BaseJson setCreated(String created) {
        this.put("_created", created);
        return this;
    }

    public String getModified() {
        return (String) this.get("_modified");
    }

    public BaseJson setModified(String modified) {
        this.put("_modified", modified);
        return this;
    }
}
