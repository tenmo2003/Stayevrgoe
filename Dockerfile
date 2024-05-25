FROM openjdk:17-alpine

WORKDIR /app

COPY ./target/stayevrgoe-0.0.1-SNAPSHOT.jar /app

COPY ./certs /app

ENTRYPOINT ["java", "-jar", "stayevrgoe-0.0.1-SNAPSHOT.jar"]

EXPOSE 8090