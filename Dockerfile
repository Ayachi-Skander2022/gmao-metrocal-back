# Utiliser une image légère avec OpenJDK 21
FROM eclipse-temurin:21-jdk-alpine

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier JAR généré dans l'image
COPY target/*.jar app.jar

# Exposer le port Spring Boot (par défaut 8080)
EXPOSE 8080

# Lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
