FROM openjdk:17-jdk-slim
WORKDIR /opt
ENV PORT 8080
EXPOSE 8080

# ENV SPRING_PROFILES_ACTIVE=default
ENV JAVA_OPTS="-Xms1800M -Xmx1800M"

COPY target/*.jar /opt/app.jar

ENTRYPOINT exec java $JAVA_OPTS -jar app.jar
