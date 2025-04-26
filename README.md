# ![RealWorld Example App](logo.png)

> ### **Java 21 + Spring Boot 3** codebase containing real-world examples (CRUD, authentication, advanced patterns, etc.) that adheres to the [RealWorld](https://github.com/gothinkster/realworld) specification and API.

### [Demo](https://demo.realworld.io/) &nbsp;&nbsp;&nbsp;&nbsp; [RealWorld](https://github.com/gothinkster/realworld)

This codebase demonstrates a fully-fledged fullstack application built with **Java 21 + Spring Boot 3**, including CRUD operations, authentication, routing, pagination, and more.

If you want to see the API documentation related to this, please refer to https://1chz.github.io/realworld-java21-springboot3

For more information on how this works with other frontend/backends, visit the [RealWorld](https://github.com/gothinkster/realworld) repository.

---

## Table of Contents

* [Considerations](#considerations)
* [How it Works](#how-it-works)
    * [Project Structure](#project-structure)
        * [Modules](#modules)
        * [Classes](#classes)
* [Database Architecture](#database-architecture)
* [Getting Started](#getting-started)
    * [API Documentation](#api-documentation)
    * [Run Application](#run-application)
    * [Apply Code Style](#apply-code-style)
    * [Run Tests](#run-tests)
    * [Run Build](#run-build)
    * [Run E2E Tests](#run-e2e-tests)
        * [Performance](#performance)

---

## Considerations

While implementing this application, the following considerations were made:

1. **Error Handling:** The application handles errors gracefully and logs them appropriately.
2. **Scalability:** Designed to scale for large volumes of data.
3. **Security:** Protects against common attacks and implements robust authentication and authorization.
4. **Code Quality:** Clean, maintainable code that follows best practices.
5. **Performance:** Optimized for frequently used operations through database indexing, query optimization, and caching.
6. **OAuth 2.0 Resource Server:** Used for simpler JWT implementation compared to manual implementations.
7. **Spring Framework:** Tightly coupled with Spring due to its robustness and backward compatibility within the Java ecosystem.
8. **Database Abstraction:** Implemented with JPA to simplify database changes.
9. **H2 Database:** Used in MySQL mode to provide similar functionality to MySQL.
10. **Input Validation:** Not included but can easily be implemented using JSR-303 (Bean Validation 2.0).
11. **Slug and Title Uniqueness:** Ensured uniqueness according to API specifications.
12. **Dependencies:** Structured to converge on the domain package for better organization.

---

## How it Works

The RealWorld project aims to create mini-blog applications with identical specifications using various technology stacks, allowing for easy comparison.

This application provides the following key features:

1. User authentication via JWT (login/signup pages, logout button on settings page)
2. Create, read, and update users (sign up & settings page; deleting users is not required)
3. CRUD operations for articles (offset-based pagination, filtering by facets, sorting)
4. Create, read, and delete comments on articles (updating comments is not required)
5. Favorite articles
6. Follow other users

### Project Structure

The project is implemented using Java 21 and Spring Boot 3, leveraging technologies such as Spring MVC, Spring Data JPA, and Spring Security. It uses H2 DB (in-memory, MySQL mode) as the database and JUnit 5 for testing.

To run the project, ensure that JDK (or JRE) 21 is installed. Then, execute the following command in the project root directory:

```shell
./gradlew realworld:bootRun
```

Access the application at [http://localhost:8080](http://localhost:8080) in your browser.

#### Modules

The project is organized into library and server modules:

-  **Library Modules** (located in the `module` directory):
    - `core`: Contains core logic, domain models, service interfaces, and exception handling.
    - `persistence`: Contains persistence layer logic, repositories, and entities.

-  **Server Modules** (located in the `server` directory):
    - `api`: Contains the API layer logic, controllers, DTOs, and exception handling.

All modules depend on the core module, following the Dependency Inversion Principle (DIP).

#### Classes

-  **Controller:** Handles HTTP requests, invokes business logic, and generates responses.
-  **Service:** Implements business logic and interacts with the database via repositories.
-  **Repository:** Interfaces for database operations, implemented using Spring Data JPA.

---

## Database Architecture

> **Note:** Data types were carefully considered, but column sizes were not a primary focus.

Many developers using JPA tend to use `Long` as the ID type. However, consider whether your table will ever require storing \(2^{63}\) records.

-  [schema.sql](module/persistence/src/main/resources/schema.sql)

![ERD Image](https://github.com/shirohoo/realworld-java21-springboot3/assets/71188307/2ed3b129-f9ec-4431-8959-374f317b7224)

---

## Getting Started

> **Note:** Ensure JDK 21 is installed.
>
> **Note:** If you encounter a permission denied error when running Gradle tasks, run `chmod +x gradlew` to grant execution permission.

### API Documentation

API documentation is available at https://1chz.github.io/realworld-java21-springboot3, generated using ReDoc in HTML format.

You can also import the [api-docs/openapi.yaml](api-docs/openapi.yaml) file into Postman or Swagger UI to test the API.

### Run Application

```shell
./gradlew realworld:bootRun
```

### Apply Code Style

> **Note:** The code style task runs automatically during the build. If the code style does not match, the build will fail.

```shell
./gradlew spotlessApply
```

### Run Tests

```shell
./gradlew test
```

### Run Build

```shell
./gradlew build
```

### Run E2E Tests

1. Start the application (**important**)
2. [Run E2E tests](api-docs/README.md#running-api-tests-locally)

#### Performance

![Performance Image](https://github.com/shirohoo/realworld-java21-springboot3/assets/71188307/f74ebb9b-327d-4f31-8299-63dda175c972)
