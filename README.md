# Flaxo
[![Build Status](https://travis-ci.org/tcibinan/flaxo.svg?branch=dev)](https://travis-ci.org/tcibinan/flaxo)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/5b599e5082814d26b34c778670c9985c)](https://www.codacy.com/app/NameOfTheLaw/flaxo?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=tcibinan/flaxo&amp;utm_campaign=Badge_Grade)

An application for easy creating, managing and reporting online courses of programming.

## What?

The main underlying idea is quite clear: 

1. Teacher creates a course with flaxo which is basically a simple git repository.
2. Teacher fill the course with tests and interfaces that students are going to implement.
3. Students solve the tasks and create pull requests.
4. Flaxo make all the necessary arrangements to test and evaluate students solutions.
5. Teacher receives well-formatted stats and results of his students progress.

## Building

To build the app and run all tests. *Notice that the system should have right environment.*

```bash
./gradlew build
```

To run the app. The application can be found at [http://localhost:8080/](http://localhost:8080/).

```bash
./gradlew bootRun
```

### Building environment

To run the application you should set several system variables.

| Variable | Description |
|---|---|
| HOME_PAGE | Home page address of your application. |
| GITHUB_ID | Github OAuth App id. |
| GITHUB_SECRET | Github OAuth App secret. |
| GITHUB_WEB_HOOK_URL | Github web hook redirect absolute url. |
| TRAVIS_WEB_HOOK_URL | Travis web hook redirect absolute url. |
| MOSS_USER_ID | Moss system userid. It can be received from the email or you can find someone else's one on the web and use for you own risk. |

Integration tests also requires a few system variables.

| Variable | Description |
|---|---|
| GITHUB_TEST_NAME | Account nickname. |
| GITHUB_TEST_TOKEN | Generated account access token with `repo`, `delete_repo` scopes. The access token can be generated in github account developer settings. |
| GITHUB_REPOSITORY_ID | Repository name to perform travis tests with. |
| TRAVIS_TEST_TOKEN | Generated using travis cli client access token. |

## Travis integration

Currently travis doesn't have oauth procedure enabled 
and the only way to retrieve travis access token is to get it from the travis cli client. 
Flaxo now uses this client and it is necessary to have [ruby](https://www.ruby-lang.org/en/documentation/installation/) 
and [travis cli client](https://github.com/travis-ci/travis.rb#installation) installed on the machine.

On unix systems installation could look like the following.
```bash
sudo apt-get install ruby-full gcc libffi-dev make
gem install travis -v 1.8.8 --no-rdoc --no-ri
```

To ensure the installation's finished successfully, type `travis` in a terminal 
and a list of possible travis cli commands should be listed.

## Supported languages and tools

The flaxo system is built to be open and expandable. There is no limitations from the flaxo itself. 
Nevertheless CI vendors have lists of supported languages and tools. See travis, moss, codacy limitations.

Moreover the flaxo presents an easy way to generate courses from scratch using api or gui interface.
The teacher literally doesn't have to create a repository, generate boilerplate branches structure, 
manage build tools, look for newest tools versions, try to find ones that work well with each other.
You just click a few buttons or send a single http request and everything will be done in background.

The course can use different languages to the task itself and the tests it is verified by. Of course language
should be compatible to each other. The same with the testing framework the testing language uses.

Currently the only building tool that is used is gradle.

### Languages

- Java 8
- Kotlin 1.2
- **todo:** Python
- **todo:** R

### Testing frameworks

- Junit5
- Spek (Kotlin testing framework)
- **todo:** Pytest

## What's inside

### Integrated services

- Github
- Travis CI
- [Moss](https://theory.stanford.edu/~aiken/moss/) plagiarism detecting system
- **todo:** Codacy

### Back-end technologies

- Kotlin language
- Gradle build tool
- Spek testing framework
- Spring Boot, Spring Data, Spring Security
- Retrofit2 http client
- and even more: h2 database, Kohsuke Github api, Kotlin test, Vavr, google/jimfs, mockito-kotlin, moji, jsoup, commons-collections4

### Front-end technologies

- axios
- js-cookie
- Immutable.js

## TODO-list

- **todo:** Simple user interface where a teacher could register, authorize, authorize using github, create a course, start course plagiarism analysis, get course stats.
- **todo:** The ability to init a flaxo course from the existing repository
- **todo:** Transparent api to get course statistics in json, xls formats

## Credits

```json
{ 
  "full_name": "Andrey Tsibin",
  "email": "tsibin.andr@gmail.com",
  "vk": "https://vk.com/id24276156",
  "app_icon": "by Anton Ivanov from the Noun Project"
}
```