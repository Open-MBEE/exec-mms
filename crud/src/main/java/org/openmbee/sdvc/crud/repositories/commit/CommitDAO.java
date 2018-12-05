package org.openmbee.sdvc.crud.repositories.commit;

import java.time.Instant;
import java.util.List;
import org.openmbee.sdvc.crud.domains.Commit;

public interface CommitDAO {

    public Commit save(Commit commit);

    public Commit findById(long id);

    public Commit findByCommitId(String commitId);

    public Commit findByRefAndTimestamp(String refId, Instant timestamp);

    public Commit findLatestByRef(String refId);

    public List<Commit> findAll();

    public List<Commit> findByRefAndTimestampAndLimit(String refId, Instant timestamp, int limit);

}
