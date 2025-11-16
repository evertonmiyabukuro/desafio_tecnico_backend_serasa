FROM gradle:8.5-jdk17 AS builder
WORKDIR /teste_serasa

COPY build.gradle settings.gradle ./
COPY src ./src

RUN gradle bootJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /teste_serasa

COPY --from=builder /teste_serasa/build/libs/DesafioTecnico*.jar DesafioTecnico.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/teste_serasa/DesafioTecnico.jar"]