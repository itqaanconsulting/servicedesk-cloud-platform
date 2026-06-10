FROM eclipse-temurin:21-jre

WORKDIR /app
COPY target/technician-service-0.1.0-SNAPSHOT.jar app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
