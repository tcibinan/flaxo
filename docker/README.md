# Flaxo deployment

Flaxo is a system which consists of several independent services:

- backend server, `flaxo/backend`
- web client server, `flaxo/frontend`
- postgres database, `postgres`

Each service has its own docker image with all required dependencies and configurations. But there is no need to
start each service container independently because docker compose is configured to do so.

## Docker images

Build time environment variables.

| Variable | Description |
|---|---|
| GITHUB_USER1_NAME | First account github nickname. |
| GITHUB_USER1_TOKEN | First account github access token. |
| TRAVIS_USER1_TOKEN | First account travis access token. |
| CODACY_USER1_TOKEN | First account codacy access token. |
| GITHUB_USER2_TOKEN | Second account github access token. |
| GITHUB_USER3_TOKEN | Third account github access token. |

Several handy scripts are in the images directory:

- `./build.sh` builds all flaxo images with *tag* specified as a first argument.
- `./publish.sh` publishes all flaxo images to dockerhub by *tag* as a first argument.

To build all images with `some` tag.

```bash
./build.sh some
```

It will generate the images like the following:
- `flaxo/frontend:some`
- `flaxo/backend:some`

To build all images with the current flaxo version.

```bash
./build.sh $(../../../gradlew -q -p ../../.. version)
```

To publish all images with `some` tag.

```bash
./publish.sh some
```

## Docker compose

Run time environment variables.

| Variable | Description |
|---|---|
| REST_URL | Flaxo external rest endpoint which will be used by flaxo frontend and some integrated external services. F.e. `http://localhost:8080/rest`. |
| GITHUB_ID | Your Github OAuth App id. |
| GITHUB_SECRET | Your Github OAuth App secret. |
| GITHUB_WEB_HOOK_URL | Flaxo github webhook url which will be used by github. It is `$REST_URL`/github/hook. |
| TRAVIS_WEB_HOOK_URL |  Flaxo travis webhook url which will be used by travis. It is `$REST_URL`/travis/hook. |
| MOSS_USER_ID | Moss system user id that can be retrieved using email registration process. More info can be found [here](https://theory.stanford.edu/~aiken/moss/). |
| tag | Flaxo release to launch. |
| data_dir | Local machine directory to store database files. |
| logs_dir | Local machine directory to store flaxo logs. |

All environment variables for docker compose run should be specified in `docker/compose/.env` file like the following:

```bash
REST_URL=http://externalurl:8080/rest
GITHUB_ID=githubid
GITHUB_SECRET=githubsecret
GITHUB_WEB_HOOK_URL=http://externalurl:8080/rest/github/hook
TRAVIS_WEB_HOOK_URL=http://externalurl:8080/rest/travis/hook
MOSS_USER_ID=mossuserid
POSTGRES_USER=username
POSTGRES_PASSWORD=password
POSTGRES_DB=flaxo
tag=latestrelease
data_dir=/some/path/to/mount/containers/data
logs_dir=/some/path/to/mount/containers/logs
```

Using docker compose all flaxo images can be launched at once under `docker/compose` directory.

```bash
docker-compose up
```

To stop all containers at once from `compose` directory.

```bash
docker-compose down
```
