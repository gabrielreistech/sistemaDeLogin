FROM openjdk:21-jdk-slim

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copia o arquivo JAR gerado para o contêiner
COPY target/login-0.0.1-SNAPSHOT.jar app.jar

# Comando para executar o aplicativo
CMD ["java", "-jar", "app.jar"]
