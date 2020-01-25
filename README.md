# Backend Application for Search

## Dependencies

- Java 8
- Maven

## Instructions for running the project

1. Extract the "search-application.zip".
2. Open the terminal or git bash.
3. Change the directory to the project folder using the following command:

   cd search-application

4. Type the following to build the jar file:

   mvn package

5. In order to run the project, run the following command:

   java -jar target/search-application-0.0.1-SNAPSHOT.jar

6. Using postman and making GET request to the following URL, you can access the endpoint:

   localhost:3000/restaurants/search?q=sushi&lat=60.17045&lon=24.93147

7. In order to exit the application, press CTRL+C from the keyboard in the terminal where the application is running.

---

P.S. The bonus task for validating blurhash has been implemented, in order to check it, please uncomment the lines 83 and 107 from "src/main/java/fi/restaurants/search/searchapplication/api/SearchApplicationController.java" and save. Then repeat steps 4 and 5 in order to run the project.
