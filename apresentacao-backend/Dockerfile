# Imagem de produção do backend — multi-stage.
# Estágio 1: compila o reactor e empacota o JAR executável (repackage do Spring Boot).
# Estágio 2: runtime mínimo (JRE), usuário não root, sem toolchain de build.
# Contexto de build esperado: raiz do repositório.

FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY . .
RUN mvn -B -DskipTests -pl apresentacao-backend -am clean package

FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app
# Usuário não root
RUN groupadd --system acadlab && useradd --system --gid acadlab --home /app acadlab
COPY --from=build /build/apresentacao-backend/target/acadlab-apresentacao-backend-*.jar /app/app.jar
USER acadlab
EXPOSE 8080
# Healthcheck por TCP (a imagem não inclui actuator/curl); bash existe na base Ubuntu do temurin.
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=5 \
  CMD bash -c 'exec 3<>/dev/tcp/127.0.0.1/8080' || exit 1
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
