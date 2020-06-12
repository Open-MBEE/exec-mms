# MMS Structured Data Version Control
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/Open-MBEE/mms.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/Open-MBEE/mms/context:java)

The MMS SDVC is a collection of modules built on top of the Spring Framework and is a part of Open-MBEE. For more information about Open-MBEE, visit the [Open-MBEE Website](https://openmbee.org/)

## Developer Setup for example project
### Docker 
We suggest using Docker to set up PostgreSQL and Elasticsearch.  Installation 
instructions are found here: [Docker documentation](https://docs.docker.com/)

### Java SE Development Kit 11
Installation instructions: [JDK-11 download](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
    
### IntelliJ IDEA

1. Import Gradle Project to IntelliJ IDEA
2. Ensure that you select JDK 10 or above and search recursively for projects.
3. The `example` subproject will show you how to include the different modules to run as a Spring Boot application.

### Gradle
A gradle wrapper is included in the root of this repository and can be called from the command line with `./gradlew [command]`.

### The Example Sub Project:
1. Copy the example properties file in `example/src/main/resources/` as `application.properties`
1. Change values for all the appropriate properties. The example file holds sane values for most properties.
1. In the command line, run `docker-compose up --build` to create and start all the services from the configuration. 
1. Swagger ui at [http://localhost:8080/v3/swagger-ui.html](http://localhost:8080/v3/swagger-ui.html)
1. Use the command `docker-compose down` to stop any containers from running and to remove the containers, networks, and images created by the `docker-compose up` command. This command should always be done before any new attempts to restart the services from the configuration. 

## Running tests

See README in /example

## Built With

* [Spring](https://spring.io)


## Contributing

To learn how you can get involved in a variety of ways, please see [Contributing to OpenMBEE](https://www.openmbee.org/contribute).

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/Open-MBEE/mms-sdvc.git). 


## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details

## Structure of Modules 

TBA


