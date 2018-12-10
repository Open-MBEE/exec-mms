package org.openmbee.sdvc.crud.repositories;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openmbee.sdvc.crud.config.DbContextHolder;
import org.openmbee.sdvc.crud.controllers.BaseJson;

public abstract class BaseElasticDAOImpl implements BaseElasticDAO {

    public String getIndex() {
        return DbContextHolder.getContext().getIndex();
    }

    public Map<String, Object> findByElasticId(String elasticId) {
        return null;
    }

    public List<Map<String, Object>> findByElasticIds(Set<String> elasticIds) {
        List<Map<String, Object>> maps = new ArrayList<>();
        int i = 97;
        for (String eid : elasticIds) {
            BaseJson json = new BaseJson();
            json.setElasticId(eid);
            json.setModified("2018-12-08T01:25:00.117-0700");
            json.setId(Character.toString((char) i));
            json.setName(json.getId());
            maps.add(json);
            i++;
        }
        /*
        BaseJson baseJson = new BaseJson();
        baseJson.setId("testing");
        baseJson.setName("element1");
        baseJson.setElasticId("8a1ee2ef-078f-4f3f-ae89-66fb5e9e7bba");
        baseJson.setModified("2015-07-04T12:08:56.235-0700");

        List<Map<String, Object>> maps = new ArrayList<>();
        maps.add(baseJson);
        */
        return maps;
    }

    public void indexAll(Collection<? extends BaseJson> jsons) {

    }

    public void index(BaseJson json) {

    }
}
