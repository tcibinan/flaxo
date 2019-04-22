# Deploy

Flaxo is is distributed as on-premises solution. It means that the system is *not available not as service* but can be 
*downloaded and hosted* in your own environment with no restrictions.

The whole system is packaged in several docker container which can be easily deploy with a docker compose approach.

## Prerequisites

Deploying requires a several actions to perform beforehand.

### Github OAuth App

Github OAuth Application is required to enable GitHub authentication in Flaxo. Therefore, you have to create a 
GitHub OAuth App and collect its `id` and `secret`. Moreover, you have to set *Homepage URL* and *Authorization 
callback URL* parameters. *Homepage URL* should be just the public root endpoint of the flaxo deployment and 
*Authorization callback URL* should be basically *Homepage URL* plus `rest/github/auth/code` suffix.

More information on Github Apps can be found in 
[the official documentation](https://developer.github.com/apps/about-apps/).

### MOSS

If you are planning to analyse student submissions for plagiarism you have to get through the MOSS email registration 
process to retrieve personal `userid`. You can do so following the instructions from 
[the official site](https://theory.stanford.edu/~aiken/moss/).

## Configuration

After you've created a GitHub App and get you MOSS user id then you have to configure several flaxo deployment 
parameters.

### Parameters

#### GITHUB_ID

Created GitHub OAuth app `id`.

#### GITHUB_SECRET

Created GitHub OAuth app `secret`.

#### MOSS_USER_ID

MOSS `userid` which you can find in your email after you request it following the instructions on  
[the official site](https://theory.stanford.edu/~aiken/moss/).

#### REST_URL

Public endpoint of the Flaxo application Rest API. The address should be available from the outer network. Rest URL
consists of a *Homepage URL* or *the deployment root path* and `/rest` suffix. F.e. *http://localhost:8080/rest* or 
*http://8.8.8.8:8080/flaxo/deployment/path/rest*.

#### POSTGRES_USER

User name to connect to the packaged PostgreSQL DB.

#### POSTGRES_PASSWORD

User password to connect to the packaged PostgreSQL DB.

#### POSTGRES_DB

Database name to use in the packaged PostgreSQL DB.

#### tag

Flaxo release to use. It should be specified without `v` prefix, f.e. `v0.3` -> `0.3`. 
Latest release can be found [here](https://github.com/tcibinan/flaxo/releases).

#### data2graph_tag

Data2graph release to use. It should be specified without `v` prefix, f.e. `v0.4` -> `0.4`. 
Latest release can be found [here](https://github.com/tcibinan/data2graph/releases). 

#### data_dir

Host local directory to store packaged PostgreSQL DB files.

#### logs_dir

Host local directory to store backend logs.

## Launch

Clone the Flaxo repository.

```bash
git clone https://github.com/tcibinan/flaxo.git
```

Change current directory to the cloned repository directory.
 
```bash
cd flaxo
```

Checkout to the required flaxo release. Latest release can be found [here](https://github.com/tcibinan/flaxo/releases).

```bash
git checkout v0.3
```

Change current directory to the docker compose directory.

```bash
cd docker/compose
```

Create `.env` file and fill it with previously resolved deployment parameters.

```bash
cat > .env << EOL
GITHUB_ID=123456
GITHUB_SECRET=secretalphanumericstring12345
MOSS_USER_ID=123456
REST_URL=http://8.8.8.8:8080/rest
POSTGRES_USER=flaxo
POSTGRES_PASSWORD=tobechanged
POSTGRES_DB=flaxo
tag=0.3
data2graph_tag=0.4
data_dir=/local/path/to/db/files
logs_dir=/local/path/to/log/files
EOL
```

Boot up Flaxo ecosystem.

```bash
docker-compose up
```
