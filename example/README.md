## Swagger codegen

[Gradle Plugin](https://github.com/int128/gradle-swagger-generator-plugin)

        ../gradlew generateSwaggerCode
        
Results in build/swagger-code-*

## Swagger UI of running app

Swagger 3 UI at http://localhost:8080/v3/swagger-ui.html (not dependent on gradle codegen)

yaml at http://localhost:8080/v3/api-docs.yaml - will need to fix securitySchemes for codegen

## Run command line api test

1. run example app on localhost (see top level readme)

        npm install -g newman
    
        newman run sdvc.postman_collection.json -e localhost-env.json --delay-request 1000
        