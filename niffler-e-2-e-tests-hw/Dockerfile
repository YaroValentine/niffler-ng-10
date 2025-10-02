FROM eclipse-temurin:21-jdk

WORKDIR /niffler
ENV TZ=Europe/Moscow
COPY ./gradle ./gradle
COPY ./niffler-e-2-e-tests-hw ./niffler-e-2-e-tests-hw
COPY ./niffler-grpc-common ./niffler-grpc-common
COPY ./gradlew ./
COPY build.gradle ./
COPY ./settings.gradle ./
COPY ./gradle.properties ./

CMD ./gradlew test -Dtest.env=docker -Drepository=jpa -Duser.timezone=Europe/Moscow
