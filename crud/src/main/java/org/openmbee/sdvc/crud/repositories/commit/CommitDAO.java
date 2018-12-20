package org.openmbee.sdvc.crud.repositories.commit;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.openmbee.sdvc.crud.domains.Commit;

public interface CommitDAO {

    public Commit save(Commit commit);

    public Optional<Commit> findById(long id);

    public Optional<Commit> findByCommitId(String commitId);

    public Optional<Commit> findByRefAndTimestamp(String refId, Instant timestamp);

    public Optional<Commit> findLatestByRef(String refId);

    public List<Commit> findAll();

    public List<Commit> findByRefAndTimestampAndLimit(String refId, Instant timestamp, int limit);

}
