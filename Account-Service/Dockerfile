FROM eclipse-temurin:17-jre

WORKDIR /account-service

COPY target/Account-Service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]