FROM openjdk:17-jdk-slim

LABEL maintainer="Rahul Prasad <prasad_rahul@yahoo.co.in>"

RUN apt-get update -y && \
    apt-get install -y softhsm2 opensc && \
    rm -rf /var/lib/apt/lists/*

#Expose the port
EXPOSE 8080

COPY hsm-wrapper/target/hsm-wrapper-1.0.0.jar /app/hsm-wrapper.jar

RUN mkdir -p /app/certs

COPY hsm-wrapper/src/main/resources/ec-keypair.tmpl /app/ec-keypair.tmpl
COPY hsm-wrapper/src/main/resources/generate-ec.sh /app/generate-ec.sh
COPY hsm-wrapper/src/main/resources/entrypoint.sh /app/entrypoint.sh

COPY hsm-wrapper/src/main/resources/application.properties.docker /app/application.properties
COPY hsm-wrapper/src/main/resources/pkcs11.cfg.docker /app/pkcs11.cfg

#Execute the entrypoint script to initialize the token
RUN chmod +x /app/generate-ec.sh /app/entrypoint.sh && \
    /app/generate-ec.sh && \
    /app/entrypoint.sh

CMD ["java", "-Dspring.config.location=file:/app/application.properties", "-jar", "/app/hsm-wrapper.jar"]