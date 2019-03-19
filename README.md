# Structured Data Version Control
[![Language Grade: Java](https://img.shields.io/lgtm/grade/java/g/Open-MBEE/mms.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/Open-MBEE/mms/context:java) [![CircleCI](https://circleci.com/gh/Open-MBEE/mms.svg?style=svg)](https://circleci.com/gh/Open-MBEE/mms)

The SDVC is a collection of modules built on top of the Spring Framework.

## Developer Setup for example project
### Docker 
We suggest using Docker to set up PostgreSQL and Elasticsearch.  Installation 
instructions are found here: [Docker documentation](https://docs.docker.com/)

### Postgresql
Install postgres (PostgreSQL) 9.6, instructions for Docker: [PostgreSQL with Docker](https://hub.docker.com/_/postgres)

    docker run -e POSTGRES_PASSWORD=test123 -e POSTGRES_USER=mmsuser -e POSTGRES_DB=mms -p 5432:5432 postgres:9-alpine
    
### Elasticsearch
Install Elasticsearch 6.6.  If you use Docker instructions are available here: [Setting up Elasticsearch with Docker](https://www.elastic.co/guide/en/elasticsearch/reference/current/docker.html)

    docker run -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:6.6.2
    
### IntelliJ IDEA

1. Import Gradle Project to IntelliJ IDEA
2. Ensure that you select JDK 10 and search recursively for projects.
3. The `example` subproject will show you how to include the different modules to run as a Spring Boot application.

### Gradle
A gradle wrapper is included in the root of this repository and can be called from the command line with `./gradlew [command]`.

### The Example Sub Project:
1. Setup Run and Debug configurations. The command line run command is `./gradlew bootRun`
2. Copy the example properties file in `example/src/main/resources/` as `application.properties`
3. Change values for all the appropriate properties. The example file holds sane values for most properties.

## Running tests

Explain how to run the automated tests for this system

### End to End Tests

Explain what these tests test and why

```
Give an example
```

### Unit Tests

Explain what these tests test and why

```
Give an example
```

## Deployment

How to you deploy this to a server

## Built With

* [SpringBoot](https://spring.io/projects/spring-boot) - The web framework used


## Contributing

Please read [CONTRIBUTING.md](github.com) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/Open-MBEE/mms-sdvc.git). 


## License

This project is licensed under the Apache License 2.0 - see the [LICENSE.md](LICENSE.md) file for details

## Structure of Modules 
### authenticator
### core
### crud
### example 
### jcr 
### ldap
### sysml
### uml



