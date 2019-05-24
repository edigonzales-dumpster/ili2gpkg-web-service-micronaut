[![Build Status](https://travis-ci.org/edigonzales/ili2gpkg-web-service-micronaut.svg?branch=master)](https://travis-ci.org/edigonzales/ili2gpkg-web-service-micronaut)

# ili2gpkg-web-service-micronaut

## Create application
```
mn create-app --features=graal-native-image --features file-watch ch.so.agi.ili2gpkg 
mn create-controller Main
./gradlew eclipse
./gradlew run --continuous
```

## Google Cloud Run
```
cat keyfile.json | docker login -u _json_key --password-stdin https://gcr.io
docker push gcr.io/ili2gpkg/ili2gpkg-web-service-jvm:latest

```

