# Flaxo
[![Build Status](https://travis-ci.org/tcibinan/flaxo.svg?branch=dev)](https://travis-ci.org/tcibinan/flaxo)

An application for easy creating, managing and reporting online courses of programming.


## Deployment

First of all you should set several system variables.

| System variable  | Description  |
|---|---|
| HOME_PAGE | Home page address of your application |
| GITHUB_ID | Github app client id |
| GITHUB_SECRET | Github app client secret |

Also the github api credentials should be set for running integration tests. The access token can be found in account settings.
```
git/src/test/resources/secured.properties content:

github.username=placeholder
github.access.token=placeholder
```


To build the app and run all tests.

```bash
./gradlew build
```

To run the app.

```bash
./gradlew bootRun
```


## What's inside

- Kotlin language
- Spring Core, Data, Boot, Security
- H2 embedded database
- Kohsuke Github Api
- Spek
- Kotlin test