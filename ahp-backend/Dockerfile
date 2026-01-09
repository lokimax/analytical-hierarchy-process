# Multi-stage build for Spring Boot backend
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /build

# Copy Maven files
COPY pom.xml .
COPY ahp-core ./ahp-core
COPY ahp-backend ./ahp-backend
COPY ahp-frontend ./ahp-frontend

# Build the application
RUN apt-get update && apt-get install -y maven && \
    mvn -f /build/pom.xml clean install -DskipTests -pl ahp-core -am && \
    mvn -f /build/ahp-backend/pom.xml clean package -DskipTests && \
    mvn -f /build/ahp-backend/pom.xml spring-boot:repackage

# Runtime stage
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy the built JAR
COPY --from=builder /build/ahp-backend/target/ahp-backend-*.jar app.jar

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD java -cp app.jar org.springframework.boot.loader.JarLauncher || exit 1

EXPOSE 9000

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
