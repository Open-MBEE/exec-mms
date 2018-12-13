package org.openmbee.sdvc.crud.repositories.commit;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openmbee.sdvc.crud.repositories.BaseElasticDAOImpl;
import org.openmbee.sdvc.json.BaseJson;
import org.springframework.stereotype.Component;

@Component
public class CommitElasticDAOImpl extends BaseElasticDAOImpl implements CommitIndexDAO {

    public Map<String, Object> findByIndexId(String indexId) {
        return this.findByIndexId("projectid_node", indexId);
    }

    public List<Map<String, Object>> findByIndexIds(Set<String> indexIds) {
        return null;
    }

    public void indexAll(Collection<? extends BaseJson> jsons) throws IOException {
        this.indexAll("projectId_node", jsons);

    }

    public void index(BaseJson json) throws IOException {
        this.index("projectId_node", json);
    }

}
