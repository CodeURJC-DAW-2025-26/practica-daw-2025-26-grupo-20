# Etapa 1: Construcción del Frontend (React con React Router v7)
FROM node:22-alpine AS frontend-builder
WORKDIR /frontend

# Copiamos archivos de dependencias para aprovechar la caché de Docker
COPY frontend/package*.json ./
RUN npm install

# Copiamos el código fuente del frontend
COPY frontend/ ./

# Construimos la aplicación
RUN npm run build

# Etapa 2: Compilación del Backend (Maven + JDK 21)
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /project

# Copiamos el pom.xml y descargamos dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiamos el código fuente del backend
COPY src ./src

# Copiamos los archivos generados por el frontend a la carpeta de recursos estáticos de Spring
# Como el frontend tiene basename "/new/", lo colocamos en static/new
RUN mkdir -p src/main/resources/static/new
COPY --from=frontend-builder /frontend/build/client/ src/main/resources/static/new/

# Compilamos el proyecto y generamos el JAR
RUN mvn clean package -DskipTests

# Etapa 3: Imagen Final de Ejecución (JRE)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiamos el JAR generado
COPY --from=builder /project/target/*.jar app.jar

# Exponemos el puerto
EXPOSE 8443

# Comando de ejecución
ENTRYPOINT ["java", "-jar", "app.jar"]
