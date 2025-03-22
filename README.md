# ![RealWorld Example App](logo.png)

> ### **Java 21 + Spring Boot 3** codebase containing real world examples (CRUD, auth, advanced patterns, etc) that adheres to the [RealWorld](https://github.com/gothinkster/realworld) spec and API.

### [Demo](https://demo.realworld.io/)&nbsp;&nbsp;&nbsp;&nbsp;[RealWorld](https://github.com/gothinkster/realworld)

This codebase was created to demonstrate a fully fledged fullstack application built with **Java 21 + Spring Boot 3** including CRUD operations, authentication, routing, pagination, and more.

For more information on how this works with other frontends/backends, head over to the [RealWorld](https://github.com/gothinkster/realworld) repo.

---

## Table of Contents
* [Considerations](#considerations)
* [How it works](#how-it-works)
    * [Project structures](#project-structures)
        * [Modules](#modules)
        * [Classes](#classes)
* [Database architecture](#database-architecture)
* [Getting started](#getting-started)
    * [Run application](#run-application)
    * [Apply code style](#apply-code-style)
    * [Run test](#run-test)
    * [Run build](#run-build)
    * [Run E2E test](#run-e2e-test)
        * [Performance](#performance)

---

## Considerations
While implementing this application, I had the following considerations:

1. Error handling: Ensure that the application handles errors gracefully and logs them appropriately.
2. Scalability: Consider the scalability of the application, especially for large volumes of data.
3. Security: Protect against common attacks and implement proper authentication and authorization.
4. Code quality: Write clean, maintainable code that follows best practices.
5. Performance: Optimize for frequently used operations through database indexing, query optimization, and caching.
6. OAuth 2.0 Resource Server: Used for simpler JWT implementation compared to manual implementation.
7. Spring Framework: Tightly coupled with Spring due to its robustness and Java ecosystem's backward compatibility.
8. Database abstraction: Implemented with JPA to make database changes easier.
9. H2 Database: Used in MySQL mode to provide similar functionality to MySQL.
10. Input validation: Excluded but can be easily implemented using JSR-303(380).
11. Slug and title uniqueness: Made unique in accordance with API specifications.
12. Dependencies: Configured to converge to the domain package for better organization.

---

## How it works
The RealWorld project aims to create mini-blog applications with the same specifications using various technology stacks, allowing for a comparison between them.

This application provides the following key features:

1. Authenticate users via JWT (login/signup pages + logout button on settings page)
2. CRU- users (sign up & settings page - no deleting required)
3. CRUD Articles (offset based pagination, filtering by facets, sorting)
4. CR-D Comments on articles (no updating required)
5. Favorite articles
6. Follow other users

### Project structures
The project is implemented based on Java 21 and Spring Boot 3, utilizing various Spring technologies such as Spring MVC, Spring Data JPA, and Spring Security. It uses H2 DB (in-memory, MySQL mode) as the database and JUnit5 for writing test codes.

To run the project, JDK(or JRE) 21 must be installed first. Then, execute the `./gradlew realworld:bootRun` command in the project root directory to run the application. After that, you can use the application by accessing http://localhost:8080 in your browser.

#### Modules
The project is organized into library modules and server modules:

- **Library Modules** (located in the `module` directory):
    - `core`: Contains the core logic, domain model, service interfaces, and exception handling.
    - `persistence`: Contains the persistence layer logic, repositories, and entities.

- **Server Modules** (located in the `server` directory):
    - `api`: Contains the API layer logic, controllers, DTOs, and exception handling.

The dependencies are configured so that all modules depend on the core module, following the Dependency Inversion Principle (DIP).

#### Classes
- ~Controller: Processes HTTP requests, calls business logic, and generates responses.
- ~Service: Implements business logic and interacts with the database through Repositories.
- ~Repository: An interface for interacting with the database, implemented using Spring Data JPA.

Authentication and authorization are implemented using Spring Security with JWT-based authentication.

---

## Database architecture
> **Note:** I paid attention to data types, but did not pay much attention to size.

Many developers who use JPA tend to use Long as the id type. However, it's worth considering whether your table with an id of Long will ever need to store 2^63 records.

- [schema.sql](module/persistence/src/main/resources/schema.sql)

![image](https://github.com/shirohoo/realworld-java21-springboot3/assets/71188307/2ed3b129-f9ec-4431-8959-374f317b7224)

---

## Getting started

> **Note:** You just need to have JDK 21 installed.
>
> **Note:** If permission denied occurs when running the gradle task, enter `chmod +x gradlew` to grant the permission.

### Run application

```shell
./gradlew server:bootRun
```

### Apply code style

> **Note:** When you run the `build` task, this task runs automatically. If the code style doesn't match, the build will fail.

```shell
./gradlew spotlessApply
```

### Run test

```shell
./gradlew test
```

### Run build

```shell
./gradlew build
```

### Run E2E test

1. Run application (**important**)
2. [Run E2E test](e2e/README.md#running-api-tests-locally)

#### Performance

![image](https://github.com/shirohoo/realworld-java21-springboot3/assets/71188307/f74ebb9b-327d-4f31-8299-63dda175c972)