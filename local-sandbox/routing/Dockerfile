FROM openjdk:8u242-jdk

COPY routing*.jar /app.jar
COPY tls-certificate.pem /tls-certificate.pem
COPY tls-private-key.pem /tls-private-key.pem
EXPOSE 8888

ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=docker"]
#docker build . -t routing:0.0.1
