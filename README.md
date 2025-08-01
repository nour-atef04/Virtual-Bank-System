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
       
