# Cricket bootstrap project

This is a template of a microservice project based on the Cricket Microservices Framework. 
To start building a new microservice clone the repository as a new one and follow the instructions below.

> This is work in progress. Stay tuned. 

## Requirements

* Java 13
* Gradle build framework

## Getting started

### Building

```
git clone https://github.com/gskorupa/cricket-bootstrap.git myproject
gradle fatJar
```

### Running

```
$ java -jar build/libs/cricket-bootstrap-0.0.3.jar -r
```

OpenAPI specification can be accessed with

```
$ curl "http://localhost:8080/api"
```

### Hello service
 
|File|Description|
|---|---|
|src/main/resources/settings.json| The service configuration |
|src/main/java/myorg/myservice/Service.java | |
|src/main/java/myorg/myservice/in/http/HelloAdapter.java | |
|src/main/java/myorg/myservice/events/HelloEvent.java| |
|src/main/java/myorg/myservice/events/MyEvent.java| |
