# Structured Data Version Control
[![Language Grade: Java](https://img.shields.io/lgtm/grade/java/g/Open-MBEE/mms.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/Open-MBEE/mms/context:java) [![CircleCI](https://circleci.com/gh/Open-MBEE/mms.svg?style=svg)](https://circleci.com/gh/Open-MBEE/mms)

The SDVC is a collection of modules built on top of the Spring Framework.

## Developer Setup
### IntelliJ IDEA

1. Import Gradle Project to IntelliJ IDEA
2. Ensure that you select JDK 10 and search recursively for projects.
3. The `example` subproject will show you how to include the different modules to run as a Spring Boot application.

### The Example Sub Project:
1. Setup Run and Debug configurations. The command line run command is `./gradlew bootRun`
2. Copy the example properties file in `example/src/main/resources/` as `application.properties`
3. Change values for all the appropriate properties. The example file holds sane values for most properties.
