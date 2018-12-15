package org.openmbee.sdvc.crud.repositories.commit;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openmbee.sdvc.json.BaseJson;

public interface CommitIndexDAO {

    Map<String, Object> findByIndexId(String indexId);

    List<Map<String, Object>> findByIndexIds(Set<String> indexIds);

    void indexAll(Collection<? extends BaseJson> jsons) throws IOException;

    void index(BaseJson json) throws IOException;

    Map<String, Object> findById(String indexId) throws IOException;

    List<Map<String, Object>> findAllById(Set<String> indexIds) throws IOException;

    void deleteById(String indexId) throws IOException;

    void deleteAll(Collection<? extends BaseJson> jsons) throws IOException;

    boolean existsById(String indexId)throws IOException;

    List<Map<String,Object>> getCommitHistory(String index, String id) throws IOException;

}
