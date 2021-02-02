============
Quick Start
============

Test Setup
==========

Dependencies
------------
  Docker
    We suggest using Docker to set up PostgreSQL and Elasticsearch.  Installation instructions are found here: `Docker documentation <https://docs.docker.com/>`_

  Java SE Development Kit 11+
        Installation instructions: `JDK-11 download <https://www.oracle.com/java/technologies/javase-jdk11-downloads.html>`_

  Postgresql or Mysql 5.7
    Install postgres (PostgreSQL) 11, instructions for Docker: `PostgreSQL with Docker <https://hub.docker.com/_/postgres>`_
    `docker run -d -e POSTGRES_PASSWORD=test1234 -e POSTGRES_USER=mmsuser -e POSTGRES_DB=mms -p 5432:5432 postgres:11-alpine`
    Or mysql: `Mysql with Docker <https://hub.docker.com/_/mysql/>`_
    `docker run -d -e MYSQL_ROOT_PASSWORD=test1234 -e MYSQL_DATABASE=mms -p 3306:3306 mysql:5.7`

  Elasticsearch
    Install Elasticsearch 7.8.  If you use Docker instructions are available here: `Setting up Elasticsearch with Docker <https://www.elastic.co/guide/en/elasticsearch/reference/current/docker.html>`_
    `docker run -d -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:7.8.1`

  MinIO
    Install MinIO for artifact storage. `Instructions for MinIO <https://docs.min.io/>`_
    `docker run -d -p 9000:9000 -e "MINIO_ACCESS_KEY=admintest" -e "MINIO_SECRET_KEY=admintest" minio/minio server /data`

MMS Application
---------------
  MMS
    Install MMS and run the web server:
    ``