# Flaxo deployment

Flaxo is a system which consists of several independent services:

- backend server, `flaxo/backend`
- web client server, `flaxo/frontend`

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
| REST_URL | Outer rest url. |
| GITHUB_ID | Github OAuth App id. |
| GITHUB_SECRET | Github OAuth App secret. |
| GITHUB_WEB_HOOK_URL | Github web hook redirect url `$REST_URL/github/hook`. |
| TRAVIS_WEB_HOOK_URL | Travis web hook redirect url `$REST_URL/travis/hook`. |
| MOSS_USER_ID | Moss system userid. |

Using docker compose all `latest` flaxo images can be launched at once from `compose` directory.

```bash
docker-compose up
```

To launch flaxo images with `some` tag then additional environment variable `tag` shall be set.

```bash
export tag=some
docker-compose up
```

To launch all images with the current flaxo version.

```bash
export tag=$(../../../gradlew -q -p ../../.. version)
docker-compose up
```

To stop all containers at once from `compose` directory.

```bash
docker-compose down
```
