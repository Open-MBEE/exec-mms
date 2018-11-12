package org.openmbee.sdvc.crud.controllers;

import java.util.HashMap;

public class BaseJson extends HashMap<String, Object> {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String ELASTICID = "_elasticId";
    public static final String PROJECTID = "_projectId";
    public static final String REFID = "_refId";
    public static final String MODIFIER = "_modifier";
    public static final String MODIFIED = "_modified";
    public static final String CREATOR = "_creator";
    public static final String CREATED = "_created";

    public String getId() {
        return (String) this.get(ID);
    }

    public BaseJson setId(String id) {
        this.put(ID, id);
        return this;
    }

    public String getName() {
        return (String) this.get(NAME);
    }

    public BaseJson setName(String name) {
        this.put(NAME, name);
        return this;
    }

    public String getElasticId() {
        return (String) this.get(ELASTICID);
    }

    public BaseJson setElasticId(String elasticId) {
        this.put(ELASTICID, elasticId);
        return this;
    }

    public String getProjectId() {
        return (String) this.get(PROJECTID);
    }

    public BaseJson setProjectId(String projectId) {
        this.put(PROJECTID, projectId);
        return this;
    }

    public String getRefId() {
        return (String) this.get(REFID);
    }

    public BaseJson setRefId(String refId) {
        this.put(REFID, refId);
        return this;
    }

    public String getModifier() {
        return (String) this.get(MODIFIER);
    }

    public BaseJson setModifier(String modifier) {
        this.put(MODIFIER, modifier);
        return this;
    }

    public String getCreator() {
        return (String) this.get(CREATOR);
    }

    public BaseJson setCreator(String creator) {
        this.put(CREATOR, creator);
        return this;
    }

    public String getCreated() {
        return (String) this.get(CREATED);
    }

    public BaseJson setCreated(String created) {
        this.put(CREATED, created);
        return this;
    }

    public String getModified() {
        return (String) this.get(MODIFIED);
    }

    public BaseJson setModified(String modified) {
        this.put(MODIFIED, modified);
        return this;
    }
}
