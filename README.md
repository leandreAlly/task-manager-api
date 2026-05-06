# Task Manager REST API

![CI Pipeline](https://github.com/leandreAlly/task-manager-api/actions/workflows/main.yml/badge.svg)

A lightweight Task Manager REST API built with **Java 17 + Spring Boot 3.x** to demonstrate Agile delivery and DevOps best practices.

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/tasks` | Create a new task |
| GET | `/tasks` | Get all tasks |
| GET | `/tasks/{id}` | Get a single task |
| PUT | `/tasks/{id}/complete` | Mark a task as completed |
| DELETE | `/tasks/{id}` | Delete a task |
| PUT | `/tasks/{id}` | Update a task |
| GET | `/health` | Health check |

## Running Locally

```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`.

## Running Tests

```bash
mvn clean test
```

## Tech Stack

- Java 17
- Spring Boot 3.2.5
- Maven
- JUnit 5 + MockMvc
- GitHub Actions (CI)
