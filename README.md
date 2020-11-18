# DataIn Service

PoC mikroserwisu odbierającego dane z drivera i zapisującego je do bazy timeseries.

Komunikacja odbywa się przy użyciu protokołu gRPC

> This is work in progress. Stay tuned. 

## Requirements

* Java 13
* Maven

## Szybki start

### Budowanie pakietu

```
$ mvn clean install
```

### Uruchomienie serwisu

```
$ java -jar target/datain-service.jar -r
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
