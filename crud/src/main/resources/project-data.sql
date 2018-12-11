INSERT INTO nodes (id, indexid, initialcommit, lastcommit, nodetype, nodeid, deleted)
    VALUES (0, 'test', 'test', 'test', 1, '${projectId}', false);

INSERT INTO branches (id, branchid, branchname, tag, deleted, timestamp)
    VALUES (0, 'master', 'master', false, false, NOW());
