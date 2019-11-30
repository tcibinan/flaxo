[![Docker Cloud Build Status Frontend](https://img.shields.io/docker/cloud/build/flaxo/frontend.svg?label=frontend)](https://hub.docker.com/r/flaxo/frontend)
[![Docker Cloud Build Status Backend](https://img.shields.io/docker/cloud/build/flaxo/backend.svg?label=backend)](https://hub.docker.com/r/flaxo/backend)
[![Docker Cloud Build Status data2graph](https://img.shields.io/docker/cloud/build/flaxo/data2graph.svg?label=data2graph)](https://hub.docker.com/r/flaxo/data2graph)

Flaxo is distributed as a set of docker images. Each release or pre-release should have a corresponding docker images 
pushed to DockerHub.

## Repositories

- backend server, [flaxo/backend](https://hub.docker.com/r/flaxo/backend)
- web client server, [flaxo/frontend](https://hub.docker.com/r/flaxo/frontend)
- data2graph tool, [flaxo/data2graph](https://hub.docker.com/r/flaxo/data2graph)

## Autobuild

Flaxo DockerHub repositories are configured to autobuild Flaxo versions. 
Each time a new version of the Flaxo platform is pushed to the repository then an automated build is triggered. 
*Flaxo version is basically a git tag.*
