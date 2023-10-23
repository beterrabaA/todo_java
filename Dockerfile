FROM ubuntu:latest AS build

RUN apt-get update
RUN apt-get install openjdk-17-jdk -y

LABEL authors="beterraba"

COPY . .

RUN apt-get install maven -y
RUN mvn clean install

FROM openjdk:17-slim

EXPOSE 8080

COPY --from=build /target/todolist-1.0.0.jar app.jar


ENTRYPOINT ["java", "-jar","app.jar"]