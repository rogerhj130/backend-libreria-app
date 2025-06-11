# Etapa 1: Compilación (construye tu aplicación Spring Boot)
# Usamos la imagen de Maven que ya incluye Java y Maven
FROM maven:3.9.4-eclipse-temurin-17 AS build

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia solo los archivos necesarios para la compilación (pom.xml y la carpeta src)
# Esto es una buena práctica para que el build de Docker sea más eficiente
# y evita copiar archivos innecesarios que puedan causar problemas.
COPY pom.xml .
COPY src ./src

# Ejecuta el comando de compilación de Maven.
# Como la imagen base ya tiene Maven, usamos 'mvn' directamente, no 'mvnw'.
# Esto suele resolver problemas de permisos o interpretación del mvnw dentro del contenedor.
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (crea la imagen final con solo el JAR y Java Runtime)
# Usamos una imagen de Java más ligera, solo con el JDK necesario para ejecutar la app
FROM eclipse-temurin:17-jdk-jammy

# Establece el directorio de trabajo dentro del contenedor para la aplicación
WORKDIR /app

# Copia el archivo JAR compilado de la etapa 'build' al directorio de trabajo actual
# Asegúrate de que 'users-backend-0.0.1-SNAPSHOT.jar' coincida exactamente con el nombre
# del JAR que Maven genera en tu proyecto.
COPY --from=build /app/target/users-backend-0.0.1-SNAPSHOT.jar app.jar

# Expone el puerto en el que Spring Boot se ejecuta (por defecto 8080)
EXPOSE 8080

# Comando para ejecutar la aplicación cuando se inicia el contenedor
ENTRYPOINT ["java", "-jar", "app.jar"]