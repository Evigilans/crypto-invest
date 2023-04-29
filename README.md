The start of the project can be performed by following commands (example for Windows):
* .\mvnw.cmd clean package
* .\mvnw.cmd dockerfile:build
* cd docker
* docker-compose up

Use PRICES_FOLDER env variable to specify which folder with csv diles should be mounted to the created image.

The JavaDoc (generated with Maven plugin) is located in java_docs/ folder and edpoint documentation is available in Swagger UI /swagger-ui/index.html
 
