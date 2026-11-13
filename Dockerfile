FROM eclipse-temurin:17-jdk AS build
COPY . /mms
WORKDIR /mms
RUN ./gradlew --no-daemon bootJar --warning-mode all
RUN find . -type f -name 'example-*.jar' -not -iname '*javadoc*' -not -iname '*sources*' -not -iname '*plain*' -exec cp '{}' '/app.jar' ';' \
    && mkdir -p jacocoJars

FROM eclipse-temurin:17-jre
RUN groupadd --system mms && useradd --system --gid mms --home-dir /mms mms \
    && mkdir -p /mms/jacocoOutput && chown -R mms:mms /mms
WORKDIR /mms
COPY --from=build /app.jar /app.jar
# Optional JaCoCo agent for integration coverage; enable it through JDK_JAVA_OPTIONS.
COPY --from=build /mms/jacocoJars /mms/jacocoJars
USER mms
ENV JDK_JAVA_OPTIONS="-XX:MaxRAMPercentage=90.0"
ENTRYPOINT ["java", "--add-opens", "java.base/java.lang=ALL-UNNAMED", "-jar", "/app.jar"]
EXPOSE 8080
