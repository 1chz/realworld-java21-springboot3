# ![RealWorld Example App](logo.png)

> ### **Java 17 + Spring Boot 3** codebase containing real world examples (CRUD, auth, advanced patterns, etc) that adheres to the [RealWorld](https://github.com/gothinkster/realworld) spec and API.

### [Demo](https://demo.realworld.io/)&nbsp;&nbsp;&nbsp;&nbsp;[RealWorld](https://github.com/gothinkster/realworld)

This codebase was created to demonstrate a fully fledged fullstack application built with ****Java 17 + Spring Boot 3**** including CRUD operations, authentication, routing, pagination, and more.

I have gone to great lengths to adhere to the **Java 17 + Spring Boot 3** community styleguides & best practices.

For more information on how to this works with other frontends/backends, head over to the [RealWorld](https://github.com/gothinkster/realworld) repo.

# Considerations

While implementing this application, I had the following considerations. If there are any developers who are trying to learn by looking at this repository, or who are planning to develop something based on this repository, it might be helpful to
consider these points:

1. Proper error handling and logging: Ensure that the application handles errors gracefully and logs them appropriately. This will make it easier to debug issues in production and improve the overall reliability of the application.
2. Scalability: Consider the scalability of the application, especially if it is expected to handle a large volume of traffic or data. This includes things like database sharding, load balancing, and caching.
3. Security: Make sure the application is secure, including protecting against common attacks such as SQL injection, cross-site scripting (XSS), and cross-site request forgery (CSRF). Use proper authentication and authorization mechanisms to ensure
   that only authorized users can access sensitive data or perform critical actions.
4. Code maintainability: Write clean, maintainable code that is easy to understand and modify. Use best practices such as code commenting, code reviews, and version control to make it easier to maintain and evolve the application over time.
5. Performance: Optimize the performance of the application, especially for frequently used or resource-intensive operations. This includes things like database indexing, query optimization, and caching.
6. If you implement JWT yourself, even if you use Spring Security, you will have to write a lot of code. That's why I used OAuth 2.0 Resource Server, which makes implementation much simpler.
7. I was curious about mixing Layered Architecture and DDD style, so I tried it out myself.
8. I have encountered various frameworks such as Django, Flask, FastAPI, Express, NestJS, Ktor, Quarkus, but I have not seen any framework as great as Spring yet. (The backward compatibility guaranteed by the Java ecosystem is truly amazing.)
   Therefore, I implemented it in a tightly coupled manner with the Spring framework, assuming that the framework would not change in the future.
9. I was implemented it so that the dependency on the database layer is only through JPA. This makes it very easy to change databases. However, if you have a deep understanding of the database, you may be concerned about inefficient queries.
10. I used H2 for the database. Although H2 is a memory database, it operates in MySQL mode and provides almost the same functionality as MySQL.
11. I have excluded input validation. If necessary, it can be easily implemented using JSR-303(380) or similar technologies, so there should be no problem.
12. I struggled with whether the slug and title of the article table should be unique, but ultimately decided to make them unique in accordance with the API specification.

# How it works

The RealWorld project aims to create mini-blog applications with the same specifications using various technology stacks, allowing for a comparison between them.

This application provides the following key features:

1. User registration/login/logout
2. Article creation/viewing/editing/deletion
3. Article list viewing (with pagination, filtering, and sorting)
4. Comment creation/viewing/editing/deletion on articles
5. User profile viewing/editing

The project is implemented based on Java 17 and Spring Boot 3, utilizing various Spring technologies such as Spring MVC, Spring Data JPA, and Spring Security. It uses H2 DB (in-memory, MySQL mode) as the database and JUnit5 for writing test codes.

To run the project, JDK 17 must be installed first. Then, execute the ./gradlew bootRun command in the project root directory to run the application. After that, you can use the application by accessing http://localhost:8080 in your browser.

Taking a closer look at the project structure, the main code of the application is located in the src/main/java directory, while the test code is located in the src/test/java directory. Additionally, configuration files and such can be found in the
src/main/resources directory.

The core logic of the application is organized as follows:

- ~Controller: Processes HTTP requests, calls business logic, and generates responses.
- ~Service: Implements business logic and interacts with the database through Repositories.
- ~Repository: An interface for interacting with the database, implemented using Spring Data JPA.

Authentication and authorization management are implemented using Spring Security, with token-based authentication using JWT. Moreover, various features of Spring Boot are utilized to implement exception handling, logging, testing, and more.

Through this project, you can learn how to implement backend applications based on Spring and how to utilize various Spring technologies. Additionally, by implementing an application following the RealWorld specifications, it provides a basis for
deciding which technology stack to choose through comparisons with various other technology stacks.

# Database Architecture

> **Note:** I paid attention to data types, but did not pay much attention to size.

Many developers who use JPA tend to use Long as the id type. However, it's worth considering whether your table with an id of Long will ever need to store 2^31 records.

- [schema.sql](database/schema.sql)

<img width="1789" alt="image" src="https://user-images.githubusercontent.com/71188307/231541178-f463b813-d022-45c3-9e41-58864df46112.png">

# Getting started

> **Note:** `chmod +x gradlew` is required

## Check code style

```shell
./gradlew spotlessCheck
```

## Apply code style

```shell
./gradlew spotlessApply
```

## Run local

```shell
./gradlew bootRun
```

## Run test

> **Note:** Running this task will generate a test coverage report at `build/jacoco/html/index.html`.

```shell
./gradlew test
```

<img width="1101" alt="image" src="https://user-images.githubusercontent.com/71188307/231682992-c5b16c47-388f-4e29-80fd-3e3759464698.png">

## Run build

```shell
./gradlew build
```

## Run integration test

1. Run local (**important**)
2. [Run integration test](api/README.md#running-api-tests-locally)

<img width="1127" alt="image" src="https://user-images.githubusercontent.com/71188307/231559354-1673e52b-3be7-4d7d-922c-e34d19eff1c3.png">
