FROM alpine:latest AS build
RUN apk add curl
RUN apk add binutils

RUN curl \
--location \
--output jdk19ea.tar.gz \
https://download.java.net/java/early_access/alpine/5/binaries/openjdk-19-ea+5_linux-x64-musl_bin.tar.gz

RUN tar xzf jdk19ea.tar.gz

COPY src /src

ARG version=0.0.1

RUN jdk-19/bin/javac \
--module-source-path src/ \
--module showrequest \
-d out/classes

RUN jdk-19/bin/jar \
--create \
--module-version $version \
--main-class showrequest.Main \
--file out/showrequest-$version.jar \
-C out/classes/showrequest .

RUN jdk-19/bin/jlink \
--compress 2 \
--no-man-pages \
--no-header-files \
--strip-debug \
--module-path out/showrequest-$version.jar \
--add-modules showrequest \
--launcher showrequest=showrequest \
--output out/runtime

FROM alpine:latest
COPY --from=build out/runtime /runtime

RUN /runtime/bin/java --list-modules

CMD ["/runtime/bin/showrequest"]
