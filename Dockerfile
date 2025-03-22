FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} mtd.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","mtd.jar"]