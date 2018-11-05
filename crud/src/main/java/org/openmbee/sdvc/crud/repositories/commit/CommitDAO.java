package org.openmbee.sdvc.crud.repositories.commit;

import java.time.Instant;
import java.util.List;
import org.openmbee.sdvc.crud.domains.Commit;

public interface CommitDAO {

    public Commit save(Commit commit);

    public Commit findByCommitId(String commitId);

    public Commit findByTimestamp(Instant timestamp);

    public Commit findLatest();

    public List<Commit> findAll();
}
