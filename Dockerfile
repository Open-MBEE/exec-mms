FROM gradle:jdk11 AS build
COPY --chown=gradle:gradle . /mms
WORKDIR /mms
#RUN ./gradlew bootJar
RUN gradle bootJar
RUN ./gradlew build -x test

#COPY mms/example/build/libs/example*.jar app.jar
RUN cp --from=build mms/example/build/libs/example*.jar ./app.jar
ENTRYPOINT ["java", "--add-opens", "java.base/java.lang=ALL-UNNAMED", "-jar", "/app.jar"]

#COPY mms/example/build/libs/example*.jar app.jar
#RUN cp mms/example/build/libs/example*.jar app.jar
#CMD ["bin/sh"]
#RUN cp mms_Gradle_1:mms/example/build/libs/example*.jar /app.jar

#RUN a cp command from /mms to copy the jar 
#(not from your local dir, but from within the container)