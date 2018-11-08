    insert into nodes (id, elasticid, initialcommit, lastcommit, nodetype, sysmlid, deleted)
    values (0, 'test', 'test', 'test', 1, '${projectId}', false);

    insert into branches (id, branchid, branchname, tag, deleted, timestamp)
    values (0, 'master', 'master', false, false, NOW());