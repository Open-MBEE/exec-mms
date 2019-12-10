## Swagger codegen

        ../gradlew generateSwaggerCode
        
Results in build/swagger-code-*

## Run command line api test

1. run example app on localhost (see top level readme)

        npm install -g newman
    
        newman run sdvc.postman_collection.json -e localhost-env.json --delay-request 1000
        