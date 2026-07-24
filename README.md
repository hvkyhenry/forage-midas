# Midas

A simulation of an enterprise-scale financial transaction processing system designed to handle high-volume, asynchronous transaction ingestion and processing. This project was developed as part of the JPMorgan Chase Advanced Software Engineering Forage program.

## Tech Stack

- **Java** & **Spring Boot**: For building a robust and scalable backend.
- **Apache Kafka**: As a message broker to decouple transaction ingestion from processing, enabling high throughput.
- **Spring Data JPA** & **Hibernate**: For data persistence and ensuring transactional integrity with an in-memory H2 database.
- **REST APIs**: For service-to-service communication (incentive calculation) and exposing data securely (balance queries).
- **Maven**: For project build and dependency management.

## Getting Started

### Prerequisites

- Java (JDK) 17 or later
- Maven

### Running the Application

You can run the application using the Maven wrapper included in the project:

```bash
./mvnw spring-boot:run
```

### Running the Tests

The project includes several test suites that simulate the different tasks of the Forage program. To run them:

```bash
./mvnw test
```

## Key Architectural Solutions

Midas is architected to solve common challenges in large-scale financial systems:

1.  **High-Volume Ingestion (Solved by Kafka)**
    - **Problem**: Synchronously processing thousands of transactions per second would overload a web server, leading to timeouts and system failure.
    - **Solution**: Kafka is used to decouple transaction ingestion from processing. Incoming transaction messages are placed in a queue, allowing the system to absorb massive traffic spikes without dropping data or overwhelming downstream services.

2.  **Data Integrity & Consistency (Solved by Spring Data JPA & `@Transactional`)**
    - **Problem**: Financial ledgers must be protected from race conditions, lost updates, and inconsistent states (e.g., money is debited but never credited).
    - **Solution**: Spring's `@Transactional` annotation ensures that all database operations for a single transaction (validating users, updating balances, logging the transaction) are performed as a single, atomic unit. If any step fails, the entire operation is rolled back.

3.  **Extensibility & Distributed Logic (Solved by REST Integration)**
    - **Problem**: Adding business logic like promotional bonuses directly into the core transaction engine makes it bloated and difficult to maintain.
    - **Solution**: The system offloads the incentive calculation to an external microservice via a REST API call (`RestTemplate`). This keeps the core ledger lean and allows business rules to be updated independently.

4.  **Controlled Data Exposure (Solved by a REST Controller)**
    - **Problem**: Client applications (like a mobile app or support dashboard) need a safe way to query user data without having direct access to the database.
    - **Solution**: A dedicated REST endpoint (`GET /balance/{id}`) provides a secure, read-only API for fetching user balances, abstracting away the internal database structure.

## Key Learnings

This project demonstrates a shift from simplistic thinking to enterprise-level architectural patterns:

- **From Immediate Execution to Eventual Consistency**: Instead of processing everything in a single, blocking request, the system embraces an asynchronous, event-driven model. This ensures resilience and scalability.

- **From Monolithic Logic to Service Boundaries**: Rather than putting all logic in one place, the system uses service-oriented principles. Offloading the reward calculation to a separate API keeps the core ledger clean and specialized.

- **From Simple DB Writes to Transactional Integrity**: Moving beyond simple data updates, the project implements strict transactional safety using Spring Data JPA, which is non-negotiable for financial systems.

## What Makes This Project Stand Out

This project is a practical, hands-on implementation of the architectural patterns that power real-world, large-scale fintech applications. It effectively demonstrates how to build a system that is:

- **Scalable**: Capable of handling a high volume of transactions through asynchronous processing.
- **Resilient**: Decoupled components mean that a failure in one part (like the incentive service) doesn't bring down the entire system.
- **Maintainable**: By separating concerns (ingestion, processing, business rules), the codebase is cleaner and easier to manage and extend.
