FROM clarius.jfrog.io/clari-docker-v0-virtual/openjdk:8-jdk

WORKDIR /workspace/conductor
COPY . /workspace/conductor

RUN ./gradlew clean build
