# Etapa 1: Compilación (Build)
# Utilizamos una imagen con Maven y JDK 21 para compilar el proyecto
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# Select working Directory
WORKDIR /project

# copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# copy source code
COPY src ./src

# run mvn
RUN mvn clean package -DskipTests

#image for run no jdk or mvn 
FROM eclipse-temurin:21-jre-alpine

#select working directory
WORKDIR /app

# copy jar file
COPY --from=builder /project/target/*.jar app.jar

# Expose port
EXPOSE 8443

#  command to run
ENTRYPOINT ["java", "-jar", "app.jar"]
