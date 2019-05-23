FROM oracle/graalvm-ce:19.0.0 as graalvm
COPY . /home/app/ili2gpkg
WORKDIR /home/app/ili2gpkg
RUN gu install native-image
RUN native-image --no-server -cp build/libs/ili2gpkg-*.jar

FROM frolvlad/alpine-glibc
#RUN apk update
#RUN apk add sqlite
EXPOSE 8080
COPY --from=graalvm /home/app/ili2gpkg .
COPY --from=graalvm /opt/graalvm-ce-19.0.0/jre/lib/amd64/libsunec.so .
#COPY libsqlitejdbc.so .
#ENTRYPOINT ["./ili2gpkg", "-Dorg.sqlite.lib.path=/", "-Dorg.sqlite.lib.name=libsqlitejdbc.so"]
ENTRYPOINT ["./ili2gpkg"]
