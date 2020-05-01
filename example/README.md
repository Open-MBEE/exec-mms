## Swagger codegen

[Gradle Plugin](https://github.com/int128/gradle-swagger-generator-plugin)

        ../gradlew generateSwaggerCode
        
Results in build/swagger-code-*

## Swagger UI of running app

Swagger 3 UI at http://localhost:8080/v3/swagger-ui.html (not dependent on gradle codegen)

yaml at http://localhost:8080/v3/api-docs.yaml - will need to fix securitySchemes for codegen

### Fixes needed from generated yaml to use in codegen

- add basic auth security scheme

        basicAuth:
          type: http
          scheme: basic
          
- change schema of ElementJson to just `type: object` (otherwise client generation ignores `additionalProperties` unless using `python-experimental` which needs other changes)
- change `addtionalProperties` in openapi spec to be just `addtionalProperties: true` where it appears

## Run command line api test

1. run example app on localhost (see top level readme)

    1. copy localhost-env.json.example to localhost-env.json, change values accordingly

            npm install -g newman
    
            newman run crud.postman_collection.json -e localhost-env.json --delay-request 300
        