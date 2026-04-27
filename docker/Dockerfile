# Etapa 1: Construcción del Frontend
FROM node:22-alpine AS frontend-builder
WORKDIR /frontend
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ ./
RUN npm run build

# Etapa 2: Compilación del Backend
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /project

# Cache de dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Código fuente y recursos
COPY src ./src

# Integración del Frontend: Copiamos el build a static/new ANTES del package
# Eliminamos cualquier rastro anterior para evitar conflictos
RUN rm -rf src/main/resources/static/new && mkdir -p src/main/resources/static/new
COPY --from=frontend-builder /frontend/build/client/ src/main/resources/static/new/

# Compilamos el JAR final (sin clean para no borrar lo copiado arriba si hay plugins raros)
RUN mvn package -DskipTests

# Etapa 3: Imagen Final
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiamos el JAR (siendo específicos con el nombre que genera Maven normalmente)
COPY --from=builder /project/target/*.jar app.jar

EXPOSE 8443
ENTRYPOINT ["java", "-jar", "app.jar"]
