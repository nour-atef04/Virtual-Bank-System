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

###1. Clone the Project

`git clone https://github.com/nour-atef04/Virtual-Bank-System.git`
`cd virtual-bank-system`


       
