FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -q -DskipTests package

FROM tomcat:10.1-jdk17-temurin

COPY --from=build /app/target/WeatherApp.war /usr/local/tomcat/webapps/ROOT.war
COPY render/start-render.sh /usr/local/bin/start-render.sh

RUN chmod +x /usr/local/bin/start-render.sh

EXPOSE 10000

CMD ["/usr/local/bin/start-render.sh"]
