FROM gradle:jdk11
COPY --chown=gradle:gradle . /mms
WORKDIR /mms
#RUN gradle bootJar
RUN ./gradlew bootJar
#RUN gradle build

RUN cp /mms/example/build/libs/example*.jar /app.jar
ENTRYPOINT ["java", "--add-opens", "java.base/java.lang=ALL-UNNAMED","-jar", "/app.jar", "--spring.profiles.active=test"] 
EXPOSE 8080
EXPOSE 5432