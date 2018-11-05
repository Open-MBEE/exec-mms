    insert into nodes (id, elasticid, initialcommit, lastcommit, nodetype, sysmlid, deleted, created, modified)
    values (0, 'test', 'test', 'test', 1, '${projectId}', false, NOW(), NOW());

    insert into branches (id, branchid, branchname, tag, deleted, created, creator, modified, modifier)
    values (0, 'master', 'master', false, false, NOW(), 'admin', NOW(), 'admin');