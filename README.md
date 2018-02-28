# Camel Swagger Contract First

## Building the project

```
$ mvn clean install
```

## Running the project

```
$ mvn spring-boot:run
```

## Testing the project

To pull up the Swagger/OpenAPI spec:

```
$ curl -X GET -H 'Accept: application/json' 'http://localhost:8080/services/camel/swagger.json'
```

To get a pet by ID:

```
$ curl -X GET -H 'Accept: application/json' 'http://localhost:8080/services/camel/v2/pet/1'
```

To get a list of pets by status:

```
$ curl -X GET -H 'Accept: application/json' 'http://localhost:8080/services/camel/v2/pet/findByStatus?status=available'
```

To get a list of pets by tags:

```
$ curl -X GET -H 'Accept: application/json' 'http://localhost:8080/services/camel/v2/pet/findByTags?tags=Sphynx&tags=Evil'
```
