# Dockerfile

# jdk17 Image Start
FROM openjdk:17

ARG JAR_FILE=build/libs/foreign-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} foreign_Backend.jar
ENTRYPOINT ["java","-jar","-Duser.timezone=Asia/Seoul","foreign_Backend.jar"]