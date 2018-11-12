package org.openmbee.sdvc.crud.repositories;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openmbee.sdvc.crud.config.DbContextHolder;

public abstract class BaseElasticDAOImpl implements BaseElasticDAO {

    public String getIndex() {
        return DbContextHolder.getContext().getIndex();
    }

    public Map<String, Object> findByElasticId(String elasticId) {
        return null;
    }

    public List<Map<String, Object>> findByElasticIds(Set<String> elasticIds) {
        return null;
    }
}
