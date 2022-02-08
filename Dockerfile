FROM openjdk:17.0.2-jdk-oracle
ARG JAR_FILE
ADD target/${JAR_FILE} ${JAR_FILE}
ENTRYPOINT ["java", "-jar", "target/${JAR_FILE}.jar"]
EXPOSE 8081
