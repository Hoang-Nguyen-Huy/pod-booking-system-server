# ===== Stage 1: Build =====
FROM maven:3.9.8-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy Maven files first for dependency caching
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source and build the JAR
COPY src ./src
RUN mvn clean package -DskipTests

# ===== Stage 2: Runtime =====
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy only the built jar from previous stage
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]