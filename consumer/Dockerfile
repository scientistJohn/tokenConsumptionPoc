FROM openjdk:8-jre-slim

ARG PROPS

ENV PROPS=${PROPS}

COPY build/libs/consumer-1.0-SNAPSHOT.jar /app.jar
COPY entrypoint.sh /
RUN chmod +x entrypoint.sh
CMD ./entrypoint.sh