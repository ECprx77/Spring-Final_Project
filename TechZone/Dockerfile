# Stage 1: Build with Maven
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

# Copy Maven wrapper and pom
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -B

# Copy source and build (skip tests for faster image build; run tests in CI)
COPY src src
RUN ./mvnw package -DskipTests -B

# Stage 2: Runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user
RUN addgroup -g 1000 appgroup && adduser -u 1000 -G appgroup -D appuser

# Copy jar from builder
COPY --from=builder /app/target/*.jar app.jar

# Optional: directory for uploads (H2 uses in-memory by default)
RUN mkdir -p uploads/products && chown -R appuser:appgroup uploads

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
