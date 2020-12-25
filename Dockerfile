FROM openjdk:8-alpine

COPY target/uberjar/scimmer.jar /scimmer/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/scimmer/app.jar"]
