FROM openjdk:17-alpine
WORKDIR /app
COPY target/*.jar /app/app.jar
EXPOSE 8081
CMD ["java", "-jar", "/app/app.jar"]