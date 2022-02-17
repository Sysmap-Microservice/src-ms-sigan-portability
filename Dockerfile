FROM openjdk:17.0.2-jdk-oracle
RUN mkdir app
ARG JAR_FILE
ADD /target/${JAR_FILE} /app/src-ms-sign-portability.jar
WORKDIR /app
ENTRYPOINT java -jar src-ms-sign-portability.jar
EXPOSE 8085

