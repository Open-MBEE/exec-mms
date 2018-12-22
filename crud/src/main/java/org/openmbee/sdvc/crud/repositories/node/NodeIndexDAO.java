package org.openmbee.sdvc.crud.repositories.node;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.openmbee.sdvc.json.BaseJson;
import org.openmbee.sdvc.json.ElementJson;

public interface NodeIndexDAO {

    void indexAll(Collection<? extends BaseJson> jsons);

    void index(BaseJson json);

    Optional<ElementJson> findById(String indexId);

    List<ElementJson> findAllById(Set<String> indexIds);

    void deleteById(String indexId);

    void deleteAll(Collection<? extends BaseJson> jsons);

    boolean existsById(String indexId);

    Optional<ElementJson> getByCommitId(String commitIndexId, String nodeId);

    Optional<ElementJson> getElementLessThanOrEqualTimestamp(String nodeId, String timestamp,
        List<String> refsCommitIds);

}
