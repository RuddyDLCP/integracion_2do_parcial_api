# 1. Usamos una imagen de Maven con Java 21 para compilar el proyecto
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
# Copiamos los archivos del proyecto al contenedor
COPY . .
# Compilamos y generamos el archivo .jar (saltando los tests para ir más rápido)
RUN mvn clean package -DskipTests

# 2. Usamos una imagen ligera de Java para ejecutar la aplicación
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copiamos el .jar generado en el paso anterior
COPY --from=build /app/target/*.jar app.jar
# Exponemos el puerto 8080
EXPOSE 8080
# Comando para iniciar la aplicación activando el perfil de producción (Azure)
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]