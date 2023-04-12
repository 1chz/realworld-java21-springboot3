# ![RealWorld Example App](logo.png)

> ### **Java 17 + Spring Boot 3** codebase containing real world examples (CRUD, auth, advanced patterns, etc) that adheres to the [RealWorld](https://github.com/gothinkster/realworld) spec and API.

### [Demo](https://demo.realworld.io/)&nbsp;&nbsp;&nbsp;&nbsp;[RealWorld](https://github.com/gothinkster/realworld)

This codebase was created to demonstrate a fully fledged fullstack application built with ****Java 17 + Spring Boot 3**** including CRUD operations, authentication, routing, pagination, and more.

I have gone to great lengths to adhere to the **Java 17 + Spring Boot 3** community styleguides & best practices.

For more information on how to this works with other frontends/backends, head over to the [RealWorld](https://github.com/gothinkster/realworld) repo.

# Considerations

1. If you implement JWT yourself, even if you use Spring Security, you will have to write a lot of code. That's why I used OAuth 2.0 Resource Server, which makes implementation much simpler.
2. I was curious about mixing Layered Architecture and DDD style, so I tried it out myself.
3. I have encountered various frameworks such as Django, Flask, FastAPI, Express, NestJS, Ktor, Quarkus, but I have not seen any framework as great as Spring yet. (The backward compatibility guaranteed by the Java ecosystem is truly amazing.)
   Therefore, I implemented it in a tightly coupled manner with the Spring framework, assuming that the framework would not change in the future.
4. I was implemented it so that the dependency on the database layer is only through JPA. This makes it very easy to change databases. However, if you have a deep understanding of the database, you may be concerned about inefficient queries.
5. I used H2 for the database. Although H2 is a memory database, it operates in MySQL mode and provides almost the same functionality as MySQL.

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

```shell
./gradlew test
```

## Run build

```shell
./gradlew build
```

## Run E2E test

1. Run local (**important**)
2. [Run E2E test](api/README.md#running-api-tests-locally)

![image](https://user-images.githubusercontent.com/71188307/231535590-a24c0650-57d6-4d39-9f9b-bd8800c6c2f4.png)
