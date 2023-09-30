FROM openjdk:17-jdk-slim

WORKDIR /app
COPY ./target/Connect4Server-1.0-SNAPSHOT.jar /app/Connect4Server.jar

EXPOSE 7171

ENTRYPOINT ["java", "-jar", "Connect4Server.jar"]