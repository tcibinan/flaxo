# Build from sources

Local running of the Flaxo application requires several additional configurations. 
If you don't need to build Flaxo from sources directly please use 
[docker deploy](https://flaxo.readthedocs.io/en/latest/deploy/) 
which is more appropriate for a production environment.

To build and run Flaxo locally you should use one of the Linux distributive. 
Moreover you should do the following:

1. Install [Java 8 jdk and jre](https://openjdk.java.net/install/).
2. Install [docker](https://docs.docker.com/install/) and [docker-compose](https://docs.docker.com/compose/install/).

## Building

Before you can launch your Flaxo instance locally you should build the project. 
It includes compiling, running unit and integration tests and etc. 
Some of the build phases requires a specific environment to be configured especially integration tests.

To run Flaxo integration tests locally you should do the following:

1. Create 3 GitHub accounts.
2. Authorize one of the created GitHub accounts with Travis.
3. Authorized the same GitHub account with Codacy.
4. Generate created GitHub account access tokens with `repo`, `delete_repo` scopes in 
[github settings](https://github.com/settings/tokens).
5. Generate Travis access token for the authorized GitHub account in 
[travis profile](https://travis-ci.org/profile).
6. Generate Codacy access token for the authorized GitHub account 
[codacy account settings](https://app.codacy.com/account/apiTokens).

Once you've collected all required access tokens then configure several build environment variables.

| Variable | Description |
|---|---|
| GITHUB_USER1_NAME | First account github nickname. |
| GITHUB_USER1_TOKEN | First account github access token. |
| TRAVIS_USER1_TOKEN | First account travis access token. |
| CODACY_USER1_TOKEN | First account codacy access token. |
| GITHUB_USER2_TOKEN | Second account github access token. |
| GITHUB_USER3_TOKEN | Third account github access token. |

Once the environment is set then the Flaxo project can be built using packaged gradle build tool.

```bash
./gradlew build
```

## Running

After the Flaxo application has been built then you can launch the Flaxo instance locally.
Flaxo launching requires a specific environment to be configured.

To run Flaxo instance locally you should do the following:

1. Create Github OAuth App in [github developer settings](https://github.com/settings/developers).
2. Retrieve Moss user id using [mailing registration](https://theory.stanford.edu/~aiken/moss/).
3. Install [node.js and npm](https://nodejs.org/en/download/).
4. Install [ruby](https://www.ruby-lang.org/en/documentation/installation/) and 
[travis cli client](https://github.com/travis-ci/travis.rb#installation).
5. Install [python 3](https://www.python.org/downloads/).
6. Configure environment variables described in the [configuration section](https://flaxo.readthedocs.io/en/latest/deploy/#configuration) excluding `tag`, `data2graph_tag` and `logs_dir`.

Once the environment is set then the Flaxo services can be launched one by one.

At first, start the database container.
```bash
cd docker/docker-compose
docker-compose run -p 5433:5432 postgres
```

Then start data2graph container.
```bash
cd docker/docker-compose
docker-compose up data2graph
```

Boot up backend using packaged gradle build tool.
```bash
./gradlew bootRun
```

And finally boot up frontend at [http://localhost:8000](http://localhost:8000).
```bash
./gradlew runFrontend
```
