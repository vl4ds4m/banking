# syntax=docker/dockerfile:1
FROM amazoncorretto:25
ARG JAR_FILE
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080