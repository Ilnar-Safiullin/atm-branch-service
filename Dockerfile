# Build target
FROM gradle:8.7-jdk17 AS build
WORKDIR /app
COPY . /app/.
RUN gradle build

# Runtime target
FROM eclipse-temurin:17-jre-alpine AS develop-runtime
WORKDIR /app
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app/app.jar
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "app/app.jar"]

# Develop runtime target
FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app
COPY ./build/libs/*-SNAPSHOT.jar app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app/app.jar"]