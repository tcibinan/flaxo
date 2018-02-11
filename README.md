# Flaxo
[![Build Status](https://travis-ci.org/tcibinan/flaxo.svg?branch=dev)](https://travis-ci.org/tcibinan/flaxo)

An application for easy creating, managing and reporting online courses of programming.


## Deployment

To run tests and application you should set several system variables.

| Variable | Description |
|---|---|
| HOME_PAGE | Home page address of your application. |
| GITHUB_ID | Github OAuth App id. |
| GITHUB_SECRET | Github OAuth App secret. |
| GITHUB_WEB_HOOK_URL | Github web hook redirect absolute url. |
| GITHUB_TEST_NAME | Account nickname. *It is used for integration tests.* |
| GITHUB_TEST_TOKEN | Generated account access token with `repo`, `delete_repo` scopes. The access token can be generated in github account developer settings. *It is used for integration tests.* |

To build the app and run all tests.

```bash
./gradlew build
```

To run the app. The application can be found at http://localhost:8080/.

```bash
./gradlew bootRun
```


## What's inside

### Services
- Github integration
- **todo:** Travis integration
- **todo:** Codacy integration

### Back-end technologies
- Kotlin language
- Gradle build tool
- Spek testing framework
- Spring Boot, Spring Data, Spring Security
- and even more: h2 database, Kohsuke Github api, Kotlin test

### Front-end technologies
- React
- Redux
- axios
- js-cookie
- Immutable.js