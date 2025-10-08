FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -q -e -U -DskipTests dependency:go-offline
COPY src ./src

RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar /app/app.jar

ENV SERVER_PORT=8080
ENV SHAREIT_SERVER_URL=http://shareit-server:9090

EXPOSE 8080
ENTRYPOINT ["sh","-lc","exec java $JAVA_OPTS -jar /app/app.jar"]
