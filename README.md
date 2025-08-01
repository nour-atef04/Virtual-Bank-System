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
        -----------------------------------
        |          |          |           |
        V          V          V           V
       User     Account   Transaction   Logging

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


       
