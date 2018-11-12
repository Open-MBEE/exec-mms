package org.openmbee.sdvc.crud.repositories;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface BaseElasticDAO {

    public Map<String, Object> findByElasticId(String elasticId);

    public List<Map<String, Object>> findByElasticIds(Set<String> elasticIds);
}
