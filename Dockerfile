# ETAPA 1: Construcción (Build)
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar archivos de configuración y descargar dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente y compilar el proyecto
COPY src ./src
RUN mvn clean package -DskipTests

# ETAPA 2: Producción (Run)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiar únicamente el archivo .jar generado en la etapa anterior
COPY --from=build /app/target/ms-carrito-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto definido en application.yml
EXPOSE 8083

# Punto de entrada para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]