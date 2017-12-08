# Flaxo

An application for easy creating, managing and reporting online courses of programming.


## Deployment

First of all you should set several system variables

| System variable  | Description  |
|---|---|
| HOME_PAGE | Home page address of your application |
| GITHUB_ID | Github app client id |
| GITHUB_SECRET | Github app client secret |
| GITHUB_REDIRECT | Redirect uri for github oauth |

To build the app and run all tests

```bash
./gradlew build
```

To run the app

```bash
./gradlew bootRun
```


## What's inside

- Kotlin language
- Spring Core, Data, Boot, Security
- H2 embedded database
- Spek