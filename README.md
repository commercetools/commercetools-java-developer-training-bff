# commercetools-java-developer-training-bff

## Start BFF with maven

Right click on application file and choose run or

```
mvn spring-boot:run
```

## Start Frontend app

To handle CORS properly, start frontend server on port 3000.

```dtd
cd src/main/resources/static
http-server . -p 3000
```