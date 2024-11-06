FROM openjdk:21
WORKDIR /app
COPY ./out/artifacts/pod_booking_system_server_jar .
EXPOSE 8080
CMD ["java", "-jar", "pod-booking-system-server.jar"]