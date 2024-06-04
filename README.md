# ![RealWorld Example App](logo.png)

> ### **Java 21 + Spring Boot 3** codebase containing real world examples (CRUD, auth, advanced patterns, etc) that adheres to the [RealWorld](https://github.com/gothinkster/realworld) spec and API.

### [Demo](https://demo.realworld.io/)&nbsp;&nbsp;&nbsp;&nbsp;[RealWorld](https://github.com/gothinkster/realworld)

This codebase was created to demonstrate a fully fledged fullstack application built with ****Java 21 + Spring Boot 3**** including CRUD operations, authentication, routing, pagination, and more.

I have gone to great lengths to adhere to the **Java 21 + Spring Boot 3** community styleguide & best practices.

For more information on how to this works with other frontends/backends, head over to the [RealWorld](https://github.com/gothinkster/realworld) repo.

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
While implementing this application, I had the following considerations. If there are any developers who are trying to learn by looking at this repository, or who are planning to develop something based on this repository, it might be helpful to
consider these points:

1. Ensure that the application handles errors gracefully and logs them appropriately. This will make it easier to debug issues in production and improve the overall reliability of the application.
2. Scalability: Consider the scalability of the application, especially if it is expected to handle a large volume of traffic or data. For example, a User class can have both followers and following, but will this code work properly if the number of followers is in the hundreds of millions?
3. Make sure the application is secure, including protecting against common attacks such as SQL injection, cross-site scripting (XSS), and cross-site request forgery (CSRF). Use proper authentication and authorization mechanisms to ensure
   that only authorized users can access sensitive data or perform critical actions.
4. Write clean, maintainable code that is easy to understand and modify. Use best practices such as code commenting, code reviews, and version control to make it easier to maintain and evolve the application over time.
5. Performance: Optimize the performance of the application, especially for frequently used or resource-intensive operations. This includes things like database indexing, query optimization, and caching.
6. If you implement JWT yourself, even if you use Spring Security, you will have to write a lot of code. That's why I used OAuth 2.0 Resource Server, which makes implementation much simpler.
7. I have encountered various frameworks such as Django, Flask, FastAPI, Express, NestJS, Ktor, Quarkus, but I have not seen any framework as great as Spring yet. (The backward compatibility guaranteed by the Java ecosystem is truly amazing.)
   Therefore, I implemented it in a tightly coupled manner with the Spring framework, assuming that the framework would not change in the future.
8. I was implemented it so that the dependency on the database layer is only through JPA. This makes it very easy to change databases. However, if you have a deep understanding of the database, you may be concerned about inefficient queries.
9. I used H2 for the database. Although H2 is a memory database, it operates in MySQL mode and provides almost the same functionality as MySQL.
10. I have excluded input validation. If necessary, it can be easily implemented using JSR-303(380) or similar technologies, so there should be no problem.
11. I struggled with whether the slug and title of the article table should be unique, but ultimately decided to make them unique in accordance with the API specification.
12. Dependencies were configured to converge to the domain package as much as possible. Dependencies can be more easily identified by looking at the import statement of each class.

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
The project is implemented based on Java 21 and Spring Boot 3, utilizing various Spring technologies such as Spring MVC, Spring Data JPA, and Spring Security. It uses H2 DB (in-memory, MySQL mode) as the database. and JUnit5 for writing test codes.

To run the project, JDK(or JRE) 21 must be installed first. Then, execute the `./gradlew :bootstrap:bootRun` command in the project root directory to run the application. After that, you can use the application by accessing http://localhost:8080 in your browser.

Taking a closer look at the project structure, the main code of the application is located in the src/main/java directory, while the test code is located in the src/test/java directory. Additionally, configuration files and such can be found in the
src/main/resources directory.

The application's package dependencies and core logic are implemented as follows:

#### Modules
![image](https://github.com/shirohoo/realworld-java21-springboot3/assets/71188307/3e81ab10-8dfc-482d-8aca-6788e6ab4659)

`core`, `persistence`, `api` and `bootstrap` modules exist, and each module performs the following roles.

- bootstrap: All existing modules are put together to form an executable application.
- core: Contains the core logic of the application, including the domain model, service, and exception handling.
- persistence: Contains the persistence layer logic of the application, including the repository and entity.
- api: Contains the API layer logic of the application, including the controller and DTO.

#### Classes
- ~Controller: Processes HTTP requests, calls business logic, and generates responses.
- ~Service: Implements business logic and interacts with the database through Repositories.
- ~Repository: An interface for interacting with the database, implemented using Spring Data JPA. 

By using ~Repository in the core package as an interface, I was applied DIP so that the dependency of the persistence package is also directed to the core package.
Finally, I was configured the dependencies so that all packages in the project depend on the core package. Please refer to the import statements for each class.

Authentication and authorization management are implemented using Spring Security, with token-based authentication using JWT. Moreover, various features of Spring Boot are utilized to implement exception handling, logging, testing, and more.

Through this project, you can learn how to implement backend applications based on Spring and how to utilize various Spring technologies. Additionally, by implementing an application following the RealWorld specifications, it provides a basis for
deciding which technology stack to choose through comparisons with various other technology stacks.

---

## Database architecture
> **Note:** I paid attention to data types, but did not pay much attention to size.

Many developers who use JPA tend to use Long as the id type. However, it's worth considering whether your table with an id of Long will ever need to store 2^63 records.

- [schema.sql](database/schema.sql)

![image](https://github.com/shirohoo/realworld-java21-springboot3/assets/71188307/2ed3b129-f9ec-4431-8959-374f317b7224)

---

## Getting started

> **Note:** You just need to have JDK 21 installed.
>
> **Note:** If permission denied occurs when running the gradle task, enter `chmod +x gradlew` to grant the permission.

### Run application

```shell
./gradlew :bootstrap:bootRun
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