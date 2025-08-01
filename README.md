# Virtual Bank System – Microservices Architecture with BFF & WSO2 API Gateway
## Overview
This is a virtual banking system built to demonstrate modern microservices architecture, including:
* Java Spring Boot Microservices
* Backend for Frontend (BFF) pattern
* Kafka-based Request/Response Logging
* Centralized WSO2 API Gateway
* OAuth2 & API Key security
* Application-level routing

## Project Goals
* Demonstrate modular architecture via independent services (User, Account, Transaction, Logging)
* Implement secure and scalable communication between services
* Handle API exposure and authentication using WSO2 API Gateway
* Simplify frontend interaction via BFF service
* Log all inter-service communication using Kafka

## High-Level Architecture
                [WSO2 API Gateway]
                        |
                        V
                      [BFF]
                        |
                        V
                     Services
        -----------------------------------------------
        |          |          |           |           |
        V          V          V           V           V
       User     Account   Transaction   Logging    Discovery (Eureka Server)

## Microservices Breakdown
### 1. User Service
Handles user registration, login, and profile.

* Passwords hashed with `bcrypt`
* Endpoints:
  - `POST /users/register`
  - `POST /users/login`
  - `GET /users/{userId}/profile`

### 2. Account Service
Handles account creation, balance checking, and transfers.

* Auto-deactivates inactive accounts after 24h
* Endpoints:
  - `POST /accounts`
  - `GET /accounts/{accountId}`
  - `GET /users/{userId}/accounts`
  - `PUT /accounts/transfer`
 
### 3. Transaction Service
Handles fund transfers and transaction history.

* Two-step fund transfer (initiation + execution)
* Endpoints:
  - `POST /transactions/transfer/initiation`
  - `POST /transactions/transfer/execution`
  - `GET /accounts/{accountId}/transactions`

### 4. BFF (Backend for Frontend)
Aggregates and simplifies frontend API calls.

* Endpoints:
  - `GET /bff/dashboard/{userId}`
  - `GET /bff/dashboard/{userId}`
  - `POST /bff/transactions/transfer/execution`
 
### 5. Logging Microservice
Kafka consumer that logs all requests/responses into a dump table.

* Kafka producer in all services
* Log format:
      `{
        "message": "{escaped JSON}",
        "messageType": "REQUEST" | "RESPONSE",
        "dateTime": "{local date time}"
        }`

## Unit Testing
Each microservice includes its own unit test suite written using JUnit 5 and Mockito, ensuring proper behavior and validation of key components.

## WSO2 API Gateway
Central access point for all external clients.

* Responsiilities:
  - OAuth2 & API Key authentication
  - Routing to internal services
 
* APIs exposed:
  
    - [User Service]
      - `POST   /register`                              → Register a new user
      - `POST   /login`                                 → User login
    
    - [BFF Service]
      - `GET    /dashboard/{userId}`                    → Fetch user dashboard
      - `POST   /transactions/transfer/initiation`      → Initiate transfer
      - `POST   /transactions/transfer/execution`       → Execute transfer

* API product (`/vbank`):
this product is used to pacakage above APIs into 1 package.

## Technology Stack
* Backend: Java 21 + Spring Boot
* Messaging: Apache Kafka
* Build Tool: Maven
* Database: MySQL
* Gateway: WSO2 API Manager
* Testing: Postman

## Setup & Run Guide

### 1. Clone the Project

`git clone https://github.com/nour-atef04/Virtual-Bank-System.git`
`cd virtual-bank-system`

### 2. Start MySQL & Create Databases
Ensure MySQL is running (default port: 3306)

`CREATE DATABASE user_service;`       
`CREATE DATABASE account_service;`         
`CREATE DATABASE transaction_service;`     
`CREATE DATABASE logging_service;`      

### 3. Configure `application.properties`
For each microservice, edit `src/main/resources/application.properties`:

`spring.datasource.url=jdbc:mysql://localhost:3306/user_service`    
`spring.datasource.username={your username}`      
`spring.datasource.password={your password}`     


Update DB name accordingly for each service.

### 4. Start Kafka & Zookeeper
Start them using Docker Compose (make sure Docker is running before executing the following command):
`docker-compose up -d`

### 5. Build & Run Microservices
1. discovery-service (Eureka server, port = 8761) 
2. logging-service (port = 8085)
3. user-service (port = 8081)
4. account-service (port = 8082)
5. transaction-service (port = 8083) 
6. bff-service (port = 8084)

### 6. Postman API Testing

* Register User
       
`POST http://localhost:8081/users/register`    
`{`   
  `"username": "john.doe",`     
  `"password": "securePassword123",`      
  `"email": "john.doe@example.com",`      
  `"firstName": "John",`      
  `"lastName": "Doe"`    
`}`  

* Login User
     
`POST http://localhost:8081/users/login`    
`{`   
  `"username": "john.doe",`     
  `"password": "securePassword123"`         
`}`  

* Get User Profile
  
`GET http://localhost:8081/users/{userId}/profile`    

* Create Account
     
`POST http://localhost:8082/accounts`   
`{`    
  `"userId": "replace-with-user-id",`   
  `"accountType": "SAVINGS",`    
  `"initialBalance": 100.00`   
`}`     

* Get Account By ID
  
`GET http://localhost:8082/accounts/{accountId}`     

* Get All Accounts of a User
  
`GET http://localhost:8082/users/{userId}/accounts`     

* Initiate Transfer
  
`POST http://localhost:8083/transactions/transfer/initiation`    
`{`   
  `"fromAccountId": "from-id",`     
  `"toAccountId": "to-id",`      
  `"amount": 30.00,`    
  `"description": "Transfer to savings"`      
`}`     

* Execute Transfer
  
`POST http://localhost:8083/transactions/transfer/execution`   
`{`    
  `"transactionId": "replace-with-tx-id"`     
`}`     

* Get Transactions by Account
  
`GET http://localhost:8083/accounts/{accountId}/transactions`     

* Get Dashboard View
  
`GET http://localhost:8084/bff/dashboard/{userId}`  

## API Gateway Setup (WSO2)
This project includes a WSO2 API Gateway setup with 4 APIs and 1 API Product to expose services securely via a centralized gateway.

### 1. Start WSO2 API Manager
* Access API Publisher at: `https://localhost:9443/publisher`       

* Log in with:
`username: admin`      
`password: admin`

### 2. Import Each API
For each API ZIP:
* Go to APIs > Import API
* Choose Import as ZIP Archive
* Upload the .zip file 
* Click Save & Publish

### 3. Import the API Product
* Go to API Products > Import Product
* Upload the .zip file which has the name `vbank`
* Click Save & Publish

### 4. Getting OAuth2 Access Token & API Key
To access the APIs securely, you'll need to get either an OAuth2 access token or an API Key using the WSO2 Developer Portal.

### 5. Testing with Postman
`POST http://localhost:8280/vbank/register`  
`POST http://localhost:8280/vbank/login`     
`GET http://localhost:8280/vbank/dashboard/{userId}`    
`POST http://localhost:8280/vbank/transactions/transfer/initiation`     
`POST http://localhost:8280/vbank/transactions/transfer/execution`  

Make sure to add either the OAuth2 Access Token or the API Key when testing.
   

       
