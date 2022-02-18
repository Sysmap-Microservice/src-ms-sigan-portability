# svc-ms-sign-portability

### Run the project image from DockerHub
To run the image of this project from the DockerHub, run this comand in your command line (Terminal or CMD):
> docker push sysmapmsteam/src-ms-sign-portability:tagname

### Run the project image from DockerHub with Docker Compose
If you want to run this project with an automated process (like CI/CD), put the following code in your file named 'docker-compose.yaml':
```yaml
version: "3.4"

services:
  src-ms-sign-portability:
    container_name: src-ms-sign-portability
    image: sysmapmsteam/src-ms-sign-portability:0.0.1-SNAPSHOT
    ports:
      - "8085:8085"
```

And, into the folder that was created the docker-compose.yaml file, run the following command (Terminal or CMD):
> docker-compose up

### Run the project from IDE with Maven
To run the application from the IDE you will need to run with the `-DskipTests` Maven parameter, just like this:
> mvn clean install -DskipTests

Without this, the application will return a error like:
` There are test failures. Please refer to D:\Java_study\springboot\springboot-sugon-3\target\surefire-reports for the individual test results.`