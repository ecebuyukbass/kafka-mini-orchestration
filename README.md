Kafka Mini Orchestration

A Spring Boot based event-driven orchestration service built with Apache Kafka, Redis, docker.

This project simulates a real-world transaction orchestration flow including idempotency validation, sequential control, retry mechanisms, and external service coordination


## Features

Kafka Producer & Consumer,
Idempotent Transaction Handling (Redis),
Sequential Validation per Terminal,
Retry Mechanism with Spring Retry,
External Service Orchestration (WebClient),
Response Publishing to Kafka,
Dockerized Infrastructure

## Tech Stack

Java 17,
Spring Boot,
Spring Kafka,
Redis,
Spring Retry,
WebClient,
Docker,
Maven

## Test Scenarios

## 1) Successful Transaction
   
New transaction ID

Valid sequence

External services respond SUCCESS

Final status: SUCCESS

## 2)Duplicate Transaction

Same transactionId sent twice

Rejected by idempotency validation

Final status: FAILED

## 3)Out-of-Order Transaction

Lower transactionId than previous

Rejected by sequential validation

Final status: FAILED

## run the Project
1️) Start Infrastructure
docker compose up -d

Services:

Kafka

Zookeeper

Redis

Wiremock (mock external services)

2️) Run Application
mvn clean install
mvn spring-boot:run

Health check:

http://localhost:8080/actuator/health

Expected:

{"status":"UP"}

Test Endpoint
POST http://localhost:8080/api/kafka/send

Body:

{
  "transactionId": "tx-1001",
  "terminalId": 1,
  "amount": 300
}


## Engineering Focus

This project demonstrates:

Event-driven architecture

Idempotent system design

Distributed transaction handling

Resilience and retry strategies

Service orchestration patterns



