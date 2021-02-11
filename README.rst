
MMS Structured Data Version Control
===================================


.. image:: https://img.shields.io/lgtm/grade/java/g/Open-MBEE/mms.svg?logo=lgtm&logoWidth=18
   :target: https://lgtm.com/projects/g/Open-MBEE/mms/context:java
   :alt: Language grade: Java
 
.. image:: https://circleci.com/gh/Open-MBEE/mms.svg?style=svg
   :target: https://circleci.com/gh/Open-MBEE/mms
   :alt: CircleCI

.. image:: https://readthedocs.org/projects/model-management-system/badge/?version=latest
  :target: https://model-management-system.readthedocs.io/en/latest/?badge=latest
  :alt: Documentation Status

The MMS SDVC is a collection of modules built on top of the Spring Framework and is a part of Open-MBEE. For more information about Open-MBEE, visit the `Open-MBEE Website <https://openmbee.org/>`_

If you are interested in deploying MMS, please see the `MMSRI <https://github.com/Open-MBEE/mmsri>`_ quickstart.
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Developer Setup
---------------

Docker
^^^^^^

We suggest using Docker to set up PostgreSQL and Elasticsearch.  Installation 
instructions are found here: `Docker documentation <https://docs.docker.com/>`_

Java SE Development Kit 11
^^^^^^^^^^^^^^^^^^^^^^^^^^

Installation instructions: `JDK-11 download <https://www.oracle.com/java/technologies/javase-jdk11-downloads.html>`_

Postgresql
^^^^^^^^^^

Install postgres (PostgreSQL) 11, instructions for Docker: `PostgreSQL with Docker <https://hub.docker.com/_/postgres>`_

.. code-block::

   docker run -d -e POSTGRES_PASSWORD=test1234 -e POSTGRES_USER=mmsuser -e POSTGRES_DB=mms -p 5432:5432 postgres:11-alpine


or Mysql
^^^^^^^^

5.7 `Mysql Docker <https://hub.docker.com/_/mysql/>`_

.. code-block::

   docker run -d -e MYSQL_ROOT_PASSWORD=test1234 -e MYSQL_DATABASE=mms -p 3306:3306 mysql:5.7


Elasticsearch
^^^^^^^^^^^^^

Install Elasticsearch 7.8.  If you use Docker instructions are available here: `Setting up Elasticsearch with Docker <https://www.elastic.co/guide/en/elasticsearch/reference/current/docker.html>`_

.. code-block::

   docker run -d -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:7.8.1


Artifacts Storage
^^^^^^^^^^^^^^^^^

.. code-block::

   docker run -d -p 9000:9000 -e "MINIO_ACCESS_KEY=admintest" -e "MINIO_SECRET_KEY=admintest" minio/minio server /data


IntelliJ IDEA
^^^^^^^^^^^^^


#. Import Gradle Project to IntelliJ IDEA
#. Ensure that you select JDK 10 or above and search recursively for projects.
#. The ``example`` subproject will show you how to include the different modules to run as a Spring Boot application.

Gradle
^^^^^^

A gradle wrapper is included in the root of this repository and can be called from the command line with ``./gradlew [command]``.

The Example Sub Project:
^^^^^^^^^^^^^^^^^^^^^^^^


#. Copy the example properties file in ``example/src/main/resources/`` as ``application.properties``
#. Change values for all the appropriate properties. The example file holds sane values for most properties.
#. Setup Run and Debug configurations. The command line run command is ``./gradlew bootRun``
#. Swagger ui at `http://localhost:8080/v3/swagger-ui.html <http://localhost:8080/v3/swagger-ui.html>`_

Running tests
-------------

See README in /example

Built With
----------


* `Spring <https://spring.io>`_

Contributing
------------

To learn how you can get involved in a variety of ways, please see `Contributing to OpenMBEE <https://www.openmbee.org/contribute>`_.

Versioning
----------

We use `SemVer <http://semver.org/>`_ for versioning. For the versions available, see the `tags on this repository <https://github.com/Open-MBEE/mms.git>`_. 

License
-------

This project is licensed under the Apache License 2.0 - see the `LICENSE <LICENSE>`_ file for details

Structure of Modules
--------------------

TBA
