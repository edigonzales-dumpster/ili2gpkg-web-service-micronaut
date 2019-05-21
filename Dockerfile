FROM oracle/graalvm-ce:19.0.0 as graalvm
COPY . /home/app/ili2gpkg
WORKDIR /home/app/ili2gpkg
RUN gu install native-image
RUN native-image --no-server -cp build/libs/ili2gpkg-*.jar

FROM frolvlad/alpine-glibc
EXPOSE 8080
COPY --from=graalvm /home/app/ili2gpkg .
ENTRYPOINT ["./ili2gpkg"]
