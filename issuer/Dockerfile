FROM openjdk:8-alpine

COPY build/libs/issuer-1.0-SNAPSHOT.jar /app.jar
EXPOSE 48090
CMD ["java", "-jar","app.jar"]