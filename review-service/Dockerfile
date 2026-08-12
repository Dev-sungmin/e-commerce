FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew :review-service:bootJar -x test

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/review-service/build/libs/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]