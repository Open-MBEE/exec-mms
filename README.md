# Structured Data Version Control
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/Open-MBEE/mms-sdvc.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/Open-MBEE/mms-sdvc/context:java)

The SDVC is a collection of modules built on top of the Spring Framework.

## Developer Setup for example project
### Docker 
We suggest using Docker to set up PostgreSQL and Elasticsearch.  Installation 
instructions are found here: [Docker documentation](https://docs.docker.com/)

### Postgresql
Install postgres (PostgreSQL) 9.6, instructions for Docker: [PostgreSQL with Docker](https://hub.docker.com/_/postgres)

    docker run -d -e POSTGRES_PASSWORD=test1234 -e POSTGRES_USER=mmsuser -e POSTGRES_DB=mms -p 5432:5432 postgres:9-alpine
    
### or Mysql
5.7 [Mysql Docker](https://hub.docker.com/_/mysql/)

    docker run -d -e MYSQL_ROOT_PASSWORD=test1234 -e MYSQL_DATABASE=mms -p 3306:3306 mysql:5.7

### Elasticsearch
Install Elasticsearch 7.1.  If you use Docker instructions are available here: [Setting up Elasticsearch with Docker](https://www.elastic.co/guide/en/elasticsearch/reference/current/docker.html)

    docker run -d -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:7.1.1
    
### IntelliJ IDEA

1. Import Gradle Project to IntelliJ IDEA
2. Ensure that you select JDK 10 or above and search recursively for projects.
3. The `example` subproject will show you how to include the different modules to run as a Spring Boot application.

### Gradle
A gradle wrapper is included in the root of this repository and can be called from the command line with `./gradlew [command]`.

### The Example Sub Project:
1. Copy the example properties file in `example/src/main/resources/` as `application.properties`
1. Change values for all the appropriate properties. The example file holds sane values for most properties.
1. Setup Run and Debug configurations. The command line run command is `./gradlew bootRun`
1. Swagger ui at [http://localhost:8080/v3/swagger-ui.html](http://localhost:8080/v3/swagger-ui.html)

## Running tests

See README in /example

## Built With

* [Spring](https://spring.io)


## Contributing

TBA

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/Open-MBEE/mms-sdvc.git). 


## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details

## Structure of Modules 

TBA


