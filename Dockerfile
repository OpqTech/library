FROM maven:3.9.6-eclipse-temurin-17
WORKDIR /app
COPY target/opq-library-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"] 