FROM openjdk:17

RUN apt-get update && \
    apt-get install -y softhsm2 && \
    mkdir -p /root/softhsm/tokens

ENV SOFTHSM2_CONF=/root/softhsm/softhsm2.conf

COPY SoftHsmProvider.cfg /etc/SoftHsmProvider.cfg
COPY target/hsm-simulator.jar /app/hsm-simulator.jar

CMD ["java", "-jar", "/app/hsm-simulator.jar"]