FROM gradle:jdk11
COPY --chown=gradle:gradle . /mms
WORKDIR /mms
RUN ./gradlew build -x test
RUN ls /mms
RUN find /mms/ -name '*jar' -exec cp --backup=numbered -t /mms/temp {} +
RUN ls /mms/temp/
#RUN ./gradlew bootJar

RUN cp /mms/example/build/libs/example*.jar /app.jar
ENTRYPOINT ["java", "--add-opens", "java.base/java.lang=ALL-UNNAMED","-jar", "/app.jar"] 
EXPOSE 8080
 