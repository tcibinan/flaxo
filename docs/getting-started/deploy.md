# Deploy

Flaxo is is distributed as on-premises solution. 
It means that the system is *not available as a service* but can be *downloaded and hosted* in your own environment 
with no restrictions.

The whole system is packaged in the several docker containers which can be easily deployed using *docker compose* tool.

## Prerequisites

### Docker

Docker and docker-compose tools should be installed on the local server. 
Information on how to install the tools in your environment can be found [here](https://docs.docker.com/install/) and 
[here](https://docs.docker.com/compose/install/).

### GitHub OAuth App

Github OAuth Application is required to enable GitHub authentication in Flaxo. 
Therefore, you have to create a GitHub OAuth App and collect its `id` and `secret`. 
Moreover, you have to configure *Homepage URL* and *Authorization callback URL* GitHub OAuth App settings. 

More information on GitHub Apps can be found in 
[the official documentation](https://developer.github.com/apps/about-apps/).

| Setting | Description |
|---------|-------------|
| Homepage URL | Root endpoint of the Flaxo deployment. F.e. `http://8.8.8.8`. |
| Authorization callback URL | GitHub Authorization endpoint of the Flaxo deployment. F.e. `http://8.8.8.8:8080/rest/github/auth/code`. |

### MOSS

If you are planning to analyse student submissions for plagiarism you have to get through the MOSS email registration 
process to retrieve personal `userid`. 
You can do so following the instructions from [the official site](https://theory.stanford.edu/~aiken/moss/).

## Configuration

Once a GitHub OAuth App is created and MOSS `userid` is retrieved then you can resolve Flaxo deployment parameters.

| Variable | Description |
|----------|-------------|
| GITHUB_ID | Created GitHub OAuth app `id`. |
| GITHUB_SECRET | Created GitHub OAuth app `secret`. |
| GITPLAG_GITHUB_AUTH | Default GitHub authentication token for gitplag. |
| MOSS_USER_ID | Retrieved MOSS `userid`. |
| DEPLOYMENT_URL | URL of the Flaxo deployment server or Homepage URL. The endpoint should be available from the outer network.  F.e. `http://8.8.8.8`. |
| REST_URL | Rest endpoint of the Flaxo deployment. The endpoint should be available from the outer network.  F.e. `http://8.8.8.8:8080/rest`. |
| POSTGRES_USER | User name to connect to the packaged PostgreSQL DB. |
| POSTGRES_PASSWORD | User password to connect to the packaged PostgreSQL DB. |
| POSTGRES_DB | Database name to use by flaxo in the packaged PostgreSQL DB. |
| GITPLAG_POSTGRES_DB | Database name to use by gitplag in the packaged PostgreSQL DB. |
| tag | ![Flaxo latest pre-release](https://img.shields.io/github/release-pre/tcibinan/flaxo.svg?label=pre-release) <br> Flaxo release to use. Latest release can be found [here](https://github.com/tcibinan/flaxo/releases). |
| data2graph_tag | ![Data2Graph latest pre-release](https://img.shields.io/github/release-pre/tcibinan/data2graph.svg?label=pre-release) <br> Data2graph release to use. It should be specified without `v` prefix, f.e. `v0.4` becomes `0.4`. Latest release can be found [here](https://github.com/tcibinan/data2graph/releases). |
| gitplag_tag | ![Gitplag latest pre-release](https://img.shields.io/github/release-pre/gitplag/gitplag.svg?label=pre-release) <br> GitPlag release to use. It should be specified without `v` prefix, f.e. `v0.5.2` becomes `0.5.2`. Latest release can be found [here](https://github.com/gitplag/gitplag/releases).
| data_dir | Host local directory to store packaged PostgreSQL DB files. |
| logs_dir | Host local directory to store backend logs. |
| gitplag_dir | Host local directory to store gitplag localized files. |

## Launch

Clone the Flaxo repository.

```bash
git clone https://github.com/tcibinan/flaxo.git
```

Change current directory to the cloned repository directory.
 
```bash
cd flaxo
```

Checkout to the required Flaxo release. 
Latest release can be found [here](https://github.com/tcibinan/flaxo/releases).

```bash
git checkout 0.7
```

Change current directory to the docker compose directory.

```bash
cd docker/compose
```

Create `.env` file and fill it with the previously resolved deployment parameters.

```bash
cat > .env << EOL
GITHUB_ID=123456
GITHUB_SECRET=secretalphanumericstring12345
GITPLAG_GITHUB_AUTH=anothersecretalphanumericstring12345
MOSS_USER_ID=123456
DEPLOYMENT_URL=http://8.8.8.8
REST_URL=http://8.8.8.8:8080/rest
POSTGRES_USER=flaxo
POSTGRES_PASSWORD=tobechanged
POSTGRES_DB=flaxo
GITPLAG_POSTGRES_DB=gitplag
tag=0.7
data2graph_tag=0.4
gitplag_tag=0.5.2
data_dir=/local/path/to/db/files
logs_dir=/local/path/to/log/files
gitplag_dir=/local/path/to/gitplag/files
EOL
```

Boot up Flaxo ecosystem.

```bash
docker-compose up
```
