# Backend Challenge

You have to build a microservice that exposes a REST api with two different tables, 
users and states. Both tables should be open to creation, deletion, or update.
every request must only accept this 'Content-type: application/json'.


## Deployed App Running on HEROKU
- [Swagger](https://backchallenge-8e96dd7c7ae3.herokuapp.com/swagger-ui/index.html#/)

### Badges
[![CircleCI](https://dl.circleci.com/status-badge/img/gh/felipeha16-hub/CleanArchitecture/tree/master.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/felipeha16-hub/CleanArchitecture/tree/master)

[![Coverage Status](https://coveralls.io/repos/github/felipeha16-hub/CleanArchitecture/badge.svg?branch=feature/feherrer)](https://coveralls.io/github/felipeha16-hub/CleanArchitecture?branch=feature/feherrer)
### Features

- Create new Users with their Pokemon Ids
- Get Users list
- Get User by Id and also gathering Pokemon Names from Poke API
- Update User
- Delete User

## Pre - Requisites
- Docker installed without SUDO Permission
- Docker compose installed without SUDO
- Ports free: 8080 and 5432

## How to run the APP

```
./up_dev.sh
```

## How to run the Test

```
./up_test.sh
```

## Areas to improve

- Data should be moved from test to an external final
- Generic method should be used to mock endpoints
- A Seed migration would be useful to have an already working app with data
- The ORM is being used with Synchronize instead of migrations. Migrations would be the best option
- Deployment could be done
- It is not necessary to test every part of the project; E2E testing alone was sufficient to test the functionality.

## Errors to be fixed
- Note: D:\Archivos\JOB\Spring_Boot\CleanArchitecture\src\test\java\com\example\user\infrastructure\controller\UserE2EIT.java uses unchecked or unsafe operations.
  Note: Recompile with -Xlint:unchecked for details.
- add Mockito as an agent to your build what is described in Mockito's documentation: https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#0.3


## Techs

- Java : 21
- Spring boot : 3.4.2
- JPA - HYBERNATE
- Postgres

## Decisions made

- Clean Architecture: To be able to handle further in the future in a proper way.



- JPA - HYBERNATE: JPA-Hibernate is the standard in Spring Boot that reduces repetitive code, improves security, and facilitates maintenance.



- Docker: To make portable



- Tets: to ensure that every piece—from the database to the API response—is robust, facilitating future changes 
without fear of breaking current functionality

    - Unit Test: (JUnit 5 + Mockito) Validate pure business logic and dependency mocks.
    - Component Test: (JUnit 5 + MapStruct) Check that the mappings between DTOs, Entities and Domain are accurate.
    - Integration Test: (Spring Data JPA + H2/Postgres) Validate the actual persistence and orchestration of Use Cases with the DB.
    - E2E Test: (TestRestTemplate + AssertJ) Simular Simulate a real client consuming the API and verify the response JSON.

## Route

  - Local : [Swagger](http://localhost:8080/swagger-ui/index.html#/)

## Env vars should be defined

To find an example of the values you can use .env.example


