FROM openjdk:8-alpine

RUN wget -P /etc/apk/keys/ https://alpine-pkgs.sgerrand.com/sgerrand.rsa.pub
RUN apk add --no-cache --repository=https://apkproxy.herokuapp.com/sgerrand/alpine-pkg-leiningen leiningen=2.9.2-r0
RUN apk add --update nodejs npm
WORKDIR /project
COPY . .
RUN lein uberjar

EXPOSE 3000

CMD ["java", "-jar", "/project/target/uberjar/scimmer.jar"]
