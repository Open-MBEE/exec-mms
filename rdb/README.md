## RDB

Implementation of metadata storage and permissions (if using local user auth/local permissions)

Uses Spring Data JPA and Hibernate for ORM for main db

Tested most with PostgreSQL, should also work for MySQL and others

Currently, a separate database is created for each project, and table for each head of branch. Each table contains pointers to the latest element (json document) for that branch. 