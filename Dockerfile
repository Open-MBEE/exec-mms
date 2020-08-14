FROM openjdk:11.0.8-jdk
COPY . /mms
WORKDIR /mms
RUN ./gradlew --no-daemon build -x test

RUN cp /mms/example/build/libs/example*.jar /app.jar
ENTRYPOINT ["java", "--add-opens", "java.base/java.lang=ALL-UNNAMED","-jar", "/app.jar"] 
EXPOSE 8080
