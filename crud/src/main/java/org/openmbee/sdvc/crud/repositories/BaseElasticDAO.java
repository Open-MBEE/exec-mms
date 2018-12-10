package org.openmbee.sdvc.crud.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openmbee.sdvc.crud.controllers.BaseJson;

public interface BaseElasticDAO {

    Map<String, Object> findByElasticId(String elasticId);

    List<Map<String, Object>> findByElasticIds(Set<String> elasticIds);

    void indexAll(Collection<? extends BaseJson> jsons);

    void index(BaseJson json);


}
