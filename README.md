###Project build and run with java 11

1) Build project
    ```
   gradle build
    ```

2) Run application
    
    2.a) You can run with custom configuration .properties file
    ```
    java -jar build/libs/transfer_money-1.0-SNAPSHOT.jar path_to_file
    ```
  
    2.b) Or run with default config.properties
    ```
    java -jar build/libs/transfer_money-1.0-SNAPSHOT.jar 
    ```
    
3) API

    3.1) Create account
    ```
    curl -X POST -H "Content-Type: application/json" http://localhost:8080/accounts -d  '{"amount":500}'
    ```    
    3.2) Get account by ID
    ```
    curl -X GET http://localhost:8080/accounts/{id} 
    ```
    3.3) Get all accounts
    ```
    curl -X GET http://localhost:8080/accounts
    ```
    3.4) Delete account by ID
    ```
    curl -X DELETE http://localhost:8080/accounts/{id}
    ```
    3.5) Transfer money from one account to another
    ```
    curl -X POST -H "Content-Type: application/json" http://localhost:8080/transfers -d '{"senderAccountId":"e24733f2-ad05-47d7-aa54-abc51938f66b","receiverAccountId":"d830d1e5-9776-43ae-945a-9f8c5a3675fc","amount":400}'
    ``` 
        

        
