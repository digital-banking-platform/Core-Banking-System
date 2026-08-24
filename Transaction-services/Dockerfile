FROM eclipse-temurin:17-jre

WORKDIR /transaction-service

COPY target/Transaction-services-0.0.1-SNAPSHOT.jar transaction-service.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "transaction-service.jar"]