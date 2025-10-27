# ====== BUILD ======
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml .
COPY server/pom.xml server/pom.xml
COPY gateway/pom.xml gateway/pom.xml

RUN mvn -q -DskipTests dependency:go-offline

COPY server/src server/src
COPY gateway/src gateway/src

RUN mvn -q -DskipTests -pl gateway -am package

# ====== RUNTIME ======
FROM eclipse-temurin:21-jre-jammy AS runtime
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /workspace/gateway/target/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app/app.jar"]
