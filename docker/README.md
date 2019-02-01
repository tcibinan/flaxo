# Flaxo deployment

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

To build all images at once just run `./build.sh` under `images` directory.

### flaxo-backend

...

### flaxo-frontend

...

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

Using docker compose all flaxo images can be launched at once from `compose` directory

```bash
docker-compose up
```

To stop all containers at once from `compose` directory

```bash
docker-compose down
```
