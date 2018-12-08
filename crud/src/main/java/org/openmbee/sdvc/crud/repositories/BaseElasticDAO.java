package org.openmbee.sdvc.crud.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface BaseElasticDAO {

    Map<String, Object> findByElasticId(String elasticId);

    List<Map<String, Object>> findByElasticIds(Set<String> elasticIds);

    void indexAll(Collection<? extends Map> jsons);

    void index(Map<String, Object> json);


}
