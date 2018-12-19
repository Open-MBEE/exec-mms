package org.openmbee.sdvc.json;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseJson extends HashMap<String, Object> {

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String INDEXID = "_indexId";
    public static final String PROJECTID = "_projectId";
    public static final String REFID = "_refId";
    public static final String MODIFIER = "_modifier";
    public static final String MODIFIED = "_modified";
    public static final String CREATOR = "_creator";
    public static final String CREATED = "_created";
    public static final String COMMITID = "_commitId";

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

    public String getIndexId() {
        return (String) this.get(INDEXID);
    }

    public BaseJson setIndexId(String indexId) {
        this.put(INDEXID, indexId);
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

    public String getModified() {
        return (String) this.get(MODIFIED);
    }

    public BaseJson setModified(String modified) {
        this.put(MODIFIED, modified);
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

    public String getCommitId() {
        return (String) this.get(COMMITID);
    }

    public BaseJson setCommitId(String commitId) {
        this.put(COMMITID, commitId);
        return this;
    }

    public boolean isPartialOf(Map<String, Object> o) {
        return isPartial(this, o);
    }

    public void merge(Map<String, Object> o) {
        for (Map.Entry<String, Object> entry : o.entrySet()) {
            String attr = entry.getKey();
            if (!this.containsKey(attr)) {
                this.put(attr, o.get(attr));
            }
        }
    }

    private static boolean isPartial(Map<String, Object> map1, Map<String, Object> map2) {
        if (map1 == map2) {
            return true;
        }
        if (map1 == null || map2 == null) {
            return false;
        }
        for (Map.Entry<String, Object> entry : map1.entrySet()) {
            if (!map2.containsKey(entry.getKey())) {
                return false;
            }
            Object value1 = entry.getValue();
            Object value2 = map2.get(entry.getKey());
            if (value1 == null && value2 != null) {
                return false;
            }
            if (value1 == value2) {
                continue;
            }
            if (value1 instanceof Map) {
                if (!(value2 instanceof Map)) {
                    return false;
                } else {
                    if (!isPartial((Map<String, Object>) value1, (Map<String, Object>) value2)) {
                        return false;
                    }
                }
            } else if (value1 instanceof List) {
                if (!(value2 instanceof List)) {
                    return false;
                } else {
                    if (!isPartial((List<Object>) value1, (List<Object>) value2)) {
                        return false;
                    }
                }
            } else if (!value1.equals(value2)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isPartial(List<Object> list1, List<Object> list2) {
        if (list1 == list2) {
            return true;
        }
        if (list1 == null || list2 == null || list1.size() != list2.size()) {
            return false;
        }

        for (int i = 0; i < list1.size(); i++) {
            Map<String, Object> toTestMap = new HashMap<>();
            Map<String, Object> testAgainstMap = new HashMap<>();

            toTestMap.put("fromList", list1.get(i));
            testAgainstMap.put("fromList", list2.get(i));

            if (!isPartial(toTestMap, testAgainstMap)) {
                return false;
            }
        }
        return true;
    }
}
