JAVA REST SERVER
================

To start the server:
1. Open the project in an IDE (for example IntelliJ IDEA)
2. Set up your database connection in DBConnection.java) (the queries require specific tables, queries for creating the schemas can be found in a separate folder but data is needed for the server to function properly)
3. Run the application
4. You can now use localhost:8085/ to access the database locally or set up ./ngrok http 8085 to access database on other devices. The port can be changed in src/main/resources/application.properties.

In Mapping.java you can see the available paths for fetching/posting data.
In DB.java you can see the queries for fetching/posting data.
