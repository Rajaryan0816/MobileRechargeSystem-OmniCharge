# Stage 1: Build
FROM eclipse-temurin:17-jdk-focal as build
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jre-focal
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]


#Stage 1 → Build the JAR with Maven.
#Stage 2 → Copy the JAR into lightweight JRE image and run it.