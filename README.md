# CoderHack - Leaderboard API

A RESTful API built with **Spring Boot** and **MongoDB** to manage the leaderboard for a coding contest platform.

## Features

- CRUD operations for contest participants
- Automatic badge assignment based on score
- Leaderboard sorted by score (descending)
- Input validation and structured error responses

## Badge Logic

| Score Range | Badges Awarded |
|-------------|----------------|
| 0 | None |
| 1 – 29 | Code Ninja |
| 30 – 59 | Code Ninja, Code Champ |
| 60 – 100 | Code Ninja, Code Champ, Code Master |

Badges accumulate as a user's score increases (max 3 unique badges).

## Tech Stack

- Java 17
- Spring Boot 3.2
- Spring Data MongoDB
- Bean Validation (Jakarta)
- Lombok
- JUnit 5 + Mockito

## Prerequisites

- Java 17+
- Maven 3.8+
- MongoDB running on `localhost:27017`

## Getting Started

```bash
# Clone the repository
git clone <your-repo-url>
cd coderhack

# Run the application
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

## API Endpoints

### Register a user
```
POST /users
Content-Type: application/json

{
  "userId": "u1",
  "username": "alice"
}
```

### Get all users (sorted by score desc)
```
GET /users
```

### Get a specific user
```
GET /users/{userId}
```

### Update a user's score
```
PUT /users/{userId}
Content-Type: application/json

{
  "score": 75
}
```

### Delete a user
```
DELETE /users/{userId}
```

## HTTP Status Codes

| Code | Meaning |
|------|---------|
| 200 | OK |
| 201 | Created |
| 204 | No Content (delete) |
| 400 | Bad Request (validation error) |
| 404 | User Not Found |
| 409 | Conflict (user already exists) |
| 500 | Internal Server Error |

## Running Tests

```bash
./mvnw test
```

## Postman Collection

> Add your public Postman Collection link here after publishing.

Example: [CoderHack API Collection](https://www.postman.com/your-collection-link)

## Project Structure

```
src/
├── main/java/com/coderhack/
│   ├── CoderHackApplication.java
│   ├── controller/UserController.java
│   ├── dto/
│   │   ├── RegisterUserRequest.java
│   │   └── UpdateScoreRequest.java
│   ├── entity/User.java
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   ├── UserAlreadyExistsException.java
│   │   └── UserNotFoundException.java
│   ├── repository/UserRepository.java
│   └── service/
│       ├── UserService.java
│       └── UserServiceImpl.java
└── test/java/com/coderhack/
    └── UserServiceTest.java
```
