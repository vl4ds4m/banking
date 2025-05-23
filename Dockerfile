# syntax=docker/dockerfile:1
FROM amazoncorretto:21
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080