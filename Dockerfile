FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/mtd-0.0.1.jar
COPY ${JAR_FILE} mtd.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","mtd.jar"]