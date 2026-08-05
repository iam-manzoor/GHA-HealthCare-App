FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline
COPY src ./src
RUN mvn -B -ntp -DskipTests package && \
    find target -maxdepth 1 -type f -name "*.jar" ! -name "*.original" -exec cp {} /tmp/app.jar \;

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=build /tmp/app.jar /app/app.jar

ENV JAVA_TOOL_OPTIONS=""
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "exec java $JAVA_TOOL_OPTIONS -jar /app/app.jar"]
