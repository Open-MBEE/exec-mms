.. _quickstart:

===========
Quick Start
===========

This quick start will focus on running all dependencies and the MMS application as docker containers. This is most suitable for testing environments. For production deployments, we recommend the `MMS Reference Implementation <https://github.com/Open-MBEE/mmsri>`_ as a starting point.

Dependencies
------------

  Docker
    We suggest using Docker to set up PostgreSQL and Elasticsearch.  Installation instructions are found here: `Docker documentation <https://docs.docker.com/>`_

  Java SE Development Kit 11+
    Installation instructions: `JDK-11 download <https://www.oracle.com/java/technologies/javase-jdk11-downloads.html>`_

  Postgresql or Mysql 5.7
    Install postgres (PostgreSQL) 11, instructions for Docker: `PostgreSQL with Docker <https://hub.docker.com/_/postgres>`_
    ::

      docker run -d -e POSTGRES_PASSWORD=test1234 -e POSTGRES_USER=mmsuser -e POSTGRES_DB=mms -p 5432:5432 --name=postgres postgres:11-alpine

    Or mysql: `Mysql with Docker <https://hub.docker.com/_/mysql/>`_
    ::

      docker run -d -e MYSQL_ROOT_PASSWORD=test1234 -e MYSQL_DATABASE=mms -p 3306:3306 --name=mysql mysql:5.7

  Elasticsearch
    Install Elasticsearch 7.8.  If you use Docker instructions are available here: `Setting up Elasticsearch with Docker <https://www.elastic.co/guide/en/elasticsearch/reference/current/docker.html>`_
    ::

      docker run -d -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" --name=elasticsearch docker.elastic.co/elasticsearch/elasticsearch:7.8.1

  MinIO
    Install MinIO for artifact storage. `Instructions for MinIO <https://docs.min.io/>`_
    ::

      docker run -d -p 9000:9000 -e "MINIO_ACCESS_KEY=admintest" -e "MINIO_SECRET_KEY=admintest" --name=minio minio/minio server /data

Running MMS
-----------

  Configure the application
    Each module of MMS can define configuration properties required to function. These properties can be defined in a single location, by default, called application.properties. Place application.properties in the classpath for MMS to use the defined values. For an example, see `example application.properties <https://github.com/Open-MBEE/mms/blob/develop/example/src/main/resources/application.properties.example>`_
    Alternatively, the properties file can be given by defining the environment variable `SPRING_CONFIG_LOCATION`.

  Run the application
    First, create a container from an MMS image. In the example below, the container is using the host network for simplicity.
    ::

      docker create --name=mms --network="host" -e "SPRING_CONFIG_LOCATION=/mms.properties" openmbee/mms:4.0.0-b5

  Copy properties
    If using the above command, copy the properties file to the defined spring config location.
    ::

      docker cp mms.properties mms:/mms.properties

  Start the container
    Finally, start the container with the injected properties file.
    ::

      docker start mms

More Information
----------------

  For more information and hints, check the `docker-compose.yml <https://github.com/Open-MBEE/mms/blob/develop/docker-compose.yml>`_ in the project root to see how we stand up an instance for testing purposes.