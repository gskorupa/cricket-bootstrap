# Cricket bootstrap project

This is a template of a microservice project based on the Cricket Microservices Framework. 
To start building a new microservice clone the repository as a new one and follow the instructions below.

> This is work in progress. Stay tuned. 

## Requirements

* Java 13
* Maven build framework

## Getting started

### Building

```
$ git clone https://github.com/gskorupa/cricket-bootstrap.git myproject
$ mvn package
```

### Running

```
$ java -jar target/cricket-bootstrap.jar -r
```

OpenAPI specification of the running service can be accessed at the service path `/api`:

```
$ curl "http://localhost:8080/api"
```

### Hello service
 
|File|Description|
|---|---|
|src/main/resources/settings.json| Service configuration file |
|src/main/java/myorg/myservice/Service.java | Service kernel |
|src/main/java/myorg/myservice/in/http/HelloAdapter.java | |
|src/main/java/myorg/myservice/events/HelloEvent.java| |
|src/main/java/myorg/myservice/events/MyEvent.java| |
