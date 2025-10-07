FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY ./pom.xml ./pom.xml
COPY ./server/pom.xml ./server/pom.xml
COPY ./gateway/pom.xml ./gateway/pom.xml

RUN mvn -q -e -U -DskipTests dependency:go-offline

COPY . .

RUN mvn -q -DskipTests -pl server -am package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl

COPY --from=build /app/server/target/*.jar /app/app.jar

ENV SERVER_PORT=9090
EXPOSE 9090

ENTRYPOINT ["java","-jar","/app/app.jar"]