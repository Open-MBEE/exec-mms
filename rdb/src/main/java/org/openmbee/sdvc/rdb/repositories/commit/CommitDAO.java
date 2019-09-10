package org.openmbee.sdvc.rdb.repositories.commit;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.openmbee.sdvc.data.domains.scoped.Branch;
import org.openmbee.sdvc.data.domains.scoped.Commit;

public interface CommitDAO {

    public Commit save(Commit commit);

    public Optional<Commit> findById(long id);

    public Optional<Commit> findByCommitId(String commitId);

    public List<Commit> findAll();

    public Optional<Commit> findLatestByRef(Branch ref);

    public Optional<Commit> findByRefAndTimestamp(Branch ref, Instant timestamp);

    public List<Commit> findByRefAndTimestampAndLimit(Branch ref, Instant timestamp, int limit);
}
