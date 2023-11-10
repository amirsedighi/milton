FROM openjdk:17-jdk-slim
WORKDIR /opt
ENV PORT 8080
EXPOSE 8080

# ENV SPRING_PROFILES_ACTIVE=default
ENV JAVA_OPTS="-Xms2048M -Xmx2048M"

COPY target/*.jar /opt/app.jar

ENTRYPOINT exec java $JAVA_OPTS -jar app.jar
