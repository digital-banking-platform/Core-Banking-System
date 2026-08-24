FROM eclipse-temurin:17-jre

WORKDIR /auth-service

COPY target/Auth-Service-0.0.1-SNAPSHOT.jar auth-service.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "auth-service.jar"]