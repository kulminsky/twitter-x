# Twitter Clone API (Groovy + Spring Boot)

This project is a simplified version of a Twitter-like API, written in Groovy using Spring Boot. It provides basic functionality such as user management, posts, comments, likes, and subscriptions. The project uses MongoDB as the primary database and Redis for caching likes for performance optimization.

## Features

- **User Management**: Create, update, delete, and subscribe to users.
- **Posts**: Create, update, delete posts, and like/unlike posts.
- **Comments**: Add comments to posts, retrieve, update, and delete comments.
- **Likes**: Likes are cached in Redis and periodically synced with MongoDB.
- **Subscriptions**: Subscribe and unsubscribe from other users.
- **Scheduling**: Periodic synchronization of likes between Redis and MongoDB using `@Scheduled`.

## Technology Stack

- **Language**: Groovy
- **Framework**: Spring Boot
- **Database**: MongoDB
- **Cache**: Redis
- **Build Tool**: Gradle
- **Testing**: Spock Framework
- **Containerization**: Docker and Docker Compose

## Prerequisites

To run this project locally, you will need the following:

- **Docker** and **Docker Compose** installed on your system.
- **Java 11+** installed for running locally (not required when using Docker).

## Project Setup

### 1. Clone the repository

```bash
git clone https://github.com/kulminsky/twitter-x.git
cd twitter-x
```

### 2. Docker Compose Setup

We are using Docker Compose to set up the required services (MongoDB and Redis) for this project. The docker-compose.yml file at the root of the project will spin up MongoDB and Redis.

### 3. Start Docker Services

To start MongoDB and Redis using Docker Compose, run the following command in the project root:
```bash
docker-compose up
```

### 4. Run the Application

You can either run the application inside a Docker container or locally on your machine.

Option 1: Run with Docker
Build the Docker image and run the application using Docker:

```bash
docker build -t x .
docker run -p 8080:8080 --name x --link mongo --link redis x
```

Option 2: Run Locally
You can also run the application locally using Gradle:

```bash
./gradlew bootRun
```

The application will be available at http://localhost:8080.

### 5. Running Tests

This project uses the Spock testing framework. All tests are written for services and controllers. To run the tests, ensure that MongoDB and Redis are running, then run the following command:

```bash
./gradlew test
```

Alternatively, you can run the tests inside a Docker container using the following command:

```bash
docker-compose exec x ./gradlew test
```

### 6. Stopping Services

When you're done, you can stop all the Docker containers using the following command:

```bash
docker-compose down
```

This will stop and remove all the containers created by Docker Compose.

## Running the API

You can interact with the API using any API client (such as Postman). Below are some example requests:

### 1. Create a User
```bash
POST http://localhost:8080/api/users
```
```json
{
  "name": "John Doe",
  "email": "johndoe@example.com"
}
```

### 2. Create a Post
```bash
POST http://localhost:8080/api/posts
```
```json
{
  "content": "This is my first post!",
  "userId": "60d21b4667d0d8992e610c85"
}
```

### 3. Like a Post
```bash
POST http://localhost:8080/api/posts/{postId}/like
```

### 4. Get All Posts
```bash
GET http://localhost:8080/api/posts
```

### 5. Add a Comment to a Post
```bash
POST http://localhost:8080/api/posts/{postId}/comments
```
```json
{
  "content": "Great post!",
  "userId": "60d21b4667d0d8992e610c85"
}
```

