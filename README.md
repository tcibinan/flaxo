# Flaxo
[![Build Status](https://travis-ci.org/tcibinan/flaxo.svg?branch=dev)](https://travis-ci.org/tcibinan/flaxo)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/5b599e5082814d26b34c778670c9985c)](https://www.codacy.com/app/NameOfTheLaw/flaxo?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=tcibinan/flaxo&amp;utm_campaign=Badge_Grade)

Flaxo educational platform is a pragmatic way to organise, manage and report programming studying process.

## Principle

The educational process using flaxo platform can be described in five steps.

1. Tutor creates a course with flaxo which is basically a simple git repository.
2. Tutor fill the course tasks with tests that students are going to write implementations for.
3. Students solve the tasks and create pull requests.
4. Flaxo make all the necessary arrangements to evaluate students solutions.
5. Tutor receives well-formatted statistics of students progress.

## Key features


### Results aggregation

Flaxo aggregates results for each course task using different metrics 

- solutions testing
- code style analysis
- plagiarism analysis
- and additional configurable analysis

![course-task-statistics](screenshots/course-task.png?raw=true)


### Course generation

You can create a course from scratch just selecting languages and framework for testing.
Flaxo knows how to build a gradle project with build-in wrappers so you don't have to waste 
your time configuring project environment.

![course-creation-modal](screenshots/course-creation-modal.png?raw=true)


### Statistics export in json, csv and tsv

All courses statistics could be retrieved in one of supported formats: json, csv and tsv. 
Results can be retrieved using flaxo api as well.


### Educational flow

You can create unlimited amount of courses for free. And it is as easy as it can be.

![all-courses](screenshots/all-courses.png?raw=true)

## Courses examples

[Java data structures course](https://github.com/tcibinan/data-structures-course) (🇷🇺)

## Supported languages and tools

The flaxo system is built to be open and expandable. There is no limitations from the flaxo itself. 
Nevertheless CI vendors have lists of supported languages and tools. _See travis, moss, codacy limitations._

### Course generation

Flaxo introduces an easy way to generate courses from scratch using api or gui interface.
User doesn't have to create a repository, generate boilerplate branches structure, 
manage build tools, look for newest tools versions trying to find ones that work well with each other.
Just a few clicks or sending a single http request is enough to create educational course.

Course can have different languages for tasks and tests. But of course languages should be 
compatible to each other in terms of Flaxo. Testing framework can be configured for each
testing language as well.

#### Languages

- Java 8
- Kotlin 1.2

#### Testing frameworks

- Junit5
- Spek (Kotlin testing framework)

## Contributing

To build the app and run all tests.

```bash
./gradlew build
```

To run backend.
```bash
./gradlew bootRun
```

To run frontend at 8088 port [http://localhost:8088](http://localhost:8088).
```bash
./gradlew runFrontend
```


### Environment

Everything described below was developed and tested only on linux-based systems 
and may not work at all on windows.

*All options which are not required are used only in integration tests.*

Prerequisites:
- Github OAuth App - **required**
- [node.js + npm](https://nodejs.org/en/download/) installed - **required**
- [ruby](https://www.ruby-lang.org/en/documentation/installation/) installed - **required**
- [travis cli client](https://github.com/travis-ci/travis.rb#installation) installed - **required**
- 3 Github accounts
- One of github accounts should be authorized in [travis](https://travis-ci.org)
- The same github account should be authorized in [codacy](https://codacy.com)

#### Environment variables

To run the application and integration tests you should set several system variables.
 

| Variable | Description |
|---|---|
| REST_URL | **required**. Outer rest url. |
| GITHUB_ID | **required**. Github OAuth App id. |
| GITHUB_SECRET | **required**. Github OAuth App secret. |
| GITHUB_WEB_HOOK_URL | **required**. Github web hook redirect url `REST_URL/github/hook`. |
| TRAVIS_WEB_HOOK_URL | **required**. Travis web hook redirect url `REST_URL/travis/hook`. |
| MOSS_USER_ID | **required**. Moss system userid. |
| GITHUB_USER1_NAME | First account github nickname. |
| GITHUB_USER1_TOKEN | First account github access token. |
| TRAVIS_USER1_TOKEN | First account travis access token. |
| CODACY_USER1_TOKEN | First account codacy access token. |
| GITHUB_USER2_TOKEN | Second account github access token. |
| GITHUB_USER3_TOKEN | Third account github access token. |

Hints:

- Github id and secret are parameters of your Github OAuth App.
- Github access tokens should have `repo`, `delete_repo` scopes. It can be generated in [github account developer settings](https://github.com/settings/tokens).
- Travis access token could be retrieved from travis cli or it can be found in [travis profile](https://travis-ci.org/profile).
- Codacy access token could be generated in [codacy account settings](https://app.codacy.com/account/apiTokens).
- Moss user id can be received through mailing registration or it can be found in several github gists.

#### Frontend temporary configuration

Currently, `frontend/src/main/kotlin/org/flaxo/frontend/Configuration.kt` should be
configured manually.

```kotlin
object Configuration {
    const val SERVER_URL = "http://actual/rest/url"
}
```

#### npm

Node.js npm should be installed to build flaxo's frontend.

#### Travis integration

Currently travis platform doesn't have oauth procedure enabled 
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

## What's inside

### Integrated external services

- Github
- Travis CI
- Codacy
- [Moss](https://theory.stanford.edu/~aiken/moss/) plagiarism detection system

### Back-end technologies

- Kotlin
- Gradle
- Spring
- Spek

### Front-end technologies

- Kotlin
- Webpack
- React
- Bootstrap

## Credits

```json
{ 
  "full_name": "Andrey Tsibin",
  "email": "tsibin.andr@gmail.com",
  "telegram": "@Nameofthelaw",
  "vk": "https://vk.com/id24276156",
  "app_icon": "by Anton Ivanov from the Noun Project",
  "year": 2018,
  "city": "Saint-Petersburg"
}
```

It will be great if you add flaxo badge [![from_flaxo with_♥](https://img.shields.io/badge/from_flaxo-with_♥-blue.svg)](https://github.com/tcibinan/flaxo) to your course README.md.

```markdown
[![from_flaxo with_♥](https://img.shields.io/badge/from_flaxo-with_♥-blue.svg)](https://github.com/tcibinan/flaxo)
```
