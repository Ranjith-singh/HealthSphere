# Maven image for java 21 projects
FROM maven:3.9.9-eclipse-temurin-21 AS builder

# Specify the working directory
WORKDIR /app

# copy pom.xml to current directory
COPY pom.xml .

# prefetch/download mvn dependencies for faster execution 
RUN mvn dependency:go-offline -B

COPY /src ./src

# deletes precompliced files and adds .jar file inside /target
# build java/mvn project inside the docker image
RUN mvn clean package

# for running .jar files
FROM openjdk:21-jdk AS runner

WORKDIR /app

# copy patientService snapshot to ./app.jar
COPY --from=builder /app/target/patientService-0.0.1-SNAPSHOT.jar ./app.jar

# port
EXPOSE 4000

# run the cmd : java -jar app.jar when container starts
# searches for java executable(jdk) and the arguments -jar and app.jar
ENTRYPOINT [ "java", "-jar","app.jar" ]






