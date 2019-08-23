FROM openjdk:11-jre
COPY example/build/libs/example*.jar app.jar
ENTRYPOINT ["java", "--add-opens", "java.base/java.lang=ALL-UNNAMED", "-jar", "/app.jar"]