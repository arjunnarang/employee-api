FROM eclipse-temurin:21-jdk
WORKDIR /home
COPY target/*.jar home.jar
ENTRYPOINT ["java", "-jar", "home.jar"]