FROM gradle:7-jdk17 as builder
USER root
WORKDIR /builder
COPY src src
COPY build.gradle.kts .
RUN gradle build -x test -x integration
RUN JAR=$(ls build/libs/*.jar | grep -v "plain") \
    && cp $JAR ./server.jar

FROM openjdk:17-slim-buster
WORKDIR /app
EXPOSE 8080
COPY --from=builder /builder/server.jar .
CMD ["java", "-jar", "server.jar"]
