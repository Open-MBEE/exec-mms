package org.openmbee.sdvc.core.services;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.openmbee.sdvc.json.BaseJson;
import org.openmbee.sdvc.json.CommitJson;

public interface CommitIndexDAO {

    void indexAll(Collection<? extends BaseJson> jsons);

    void index(BaseJson json);

    Optional<CommitJson> findById(String indexId);

    List<CommitJson> findAllById(Set<String> indexIds);

    void deleteById(String indexId);

    void deleteAll(Collection<? extends BaseJson> jsons);

    boolean existsById(String indexId);

    List<CommitJson> elementHistory(String id, Set<String> commitIds);

}
