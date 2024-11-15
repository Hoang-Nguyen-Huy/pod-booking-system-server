FROM openjdk:21
WORKDIR /app
COPY target/pod-booking-system-server.jar .
EXPOSE 8080
CMD ["java", "-jar", "pod-booking-system-server.jar"]