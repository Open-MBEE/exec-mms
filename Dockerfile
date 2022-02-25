FROM openjdk:11-jdk-slim
COPY . /mms
WORKDIR /mms
RUN ./gradlew --no-daemon bootJar

RUN find . -type f -name example-*.jar -not -iname '*javadoc*' -not -iname '*sources*' -exec cp '{}' '/app.jar' ';'
ENV JDK_JAVA_OPTIONS "-XX:MaxRAMPercentage=90.0"
ENTRYPOINT ["java", "--add-opens", "java.base/java.lang=ALL-UNNAMED", "-jar", "/app.jar"]
EXPOSE 8080
