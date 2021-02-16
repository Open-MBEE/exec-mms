FROM openjdk:11.0.8-jdk
COPY . /mms
WORKDIR /mms
RUN ./gradlew --no-daemon bootJar

RUN cp /mms/example/build/libs/mms-example*.jar /app.jar
ENV JDK_JAVA_OPTIONS "-XX:MaxRAMPercentage=90.0"
ENTRYPOINT ["java", "--add-opens", "java.base/java.lang=ALL-UNNAMED", "-jar", "/app.jar"]
EXPOSE 8080
