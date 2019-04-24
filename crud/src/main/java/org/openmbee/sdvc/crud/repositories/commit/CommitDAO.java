package org.openmbee.sdvc.crud.repositories.commit;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.openmbee.sdvc.data.domains.Branch;
import org.openmbee.sdvc.data.domains.Commit;

public interface CommitDAO {

    public Commit save(Commit commit);

    public Optional<Commit> findById(long id);

    public Optional<Commit> findByCommitId(String commitId);

    public List<Commit> findAll();

    public List<Commit> findByRefAndLimit(String refId, Long cid, Instant timestamp, int limit);

    public List<Commit> findByRefAndTimestampAndLimit(Branch ref, Instant timestamp, int limit);
}
