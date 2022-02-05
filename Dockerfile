FROM openjdk:17-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
RUN mkdir -p /home/Pictures/
ENV HOME=/home
ENTRYPOINT ["java","-jar","app.jar"]
EXPOSE 8080