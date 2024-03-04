# syntax=docker/dockerfile:1
FROM amazoncorretto:21
COPY target/app.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080