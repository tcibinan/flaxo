# Flaxo
[![Build Status](https://travis-ci.org/tcibinan/flaxo.svg?branch=dev)](https://travis-ci.org/tcibinan/flaxo)

An application for easy creating, managing and reporting online courses of programming.

## What?

The main underlying idea is quite clear: 

1. Teacher creates a course with flaxo which is basically a simple git repository.
2. Teacher fill the course with tests and interfaces that students are going to implement.
3. Students solve the tasks and create pull requests.
4. Flaxo make all the necessary arrangements to test and evaluate students solutions.
5. Teacher receives well-formatted stats and results of his students progress.

## Deployment

To run tests and application you should set several system variables.

| Variable | Description |
|---|---|
| HOME_PAGE | Home page address of your application. |
| GITHUB_ID | Github OAuth App id. |
| GITHUB_SECRET | Github OAuth App secret. |
| GITHUB_WEB_HOOK_URL | Github web hook redirect absolute url. |
| GITHUB_TEST_NAME | Account nickname. *It is used for integration tests.* |
| GITHUB_TEST_TOKEN | Generated account access token with `repo`, `delete_repo`, `user:email`, `read:org`, `repo_deployment`, `repo:status`, `write:repo_hook` scopes for flaxo and travis needs. The access token can be generated in github account developer settings. *It is used for integration tests.* |
| GITHUB_REPOSITORY_ID | Repository id to perform travis tests with. *It is used for integration tests.* |
| TRAVIS_TEST_TOKEN | Generated using travis cli client access token. *It is used for integration tests.* |

### Travis integration

Currently travis doesn't have oauth procedure enabled 
and the only way to retrieve travis access token is to get it from the travis cli client. 
Flaxo now uses this client and it is necessary to have [ruby](https://www.ruby-lang.org/en/documentation/installation/) 
and [travis cli client](https://github.com/travis-ci/travis.rb#installation) installed on the machine.

On unix systems installation could look like the following.
```bash
sudo apt-get install ruby-full
gem install travis -v 1.8.8 --no-rdoc --no-ri
```

To ensure the installation's finished successfully, type `travis` in a terminal 
and a list of possible travis cli commands should be listed.

## Building

To build the app and run all tests.

```bash
./gradlew build
```

To run the app. The application can be found at [http://localhost:8080/](http://localhost:8080/).

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

## Credits

App icon - flex by  Anton Ivanov from [the Noun Project](https://thenounproject.com).