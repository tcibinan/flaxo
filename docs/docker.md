[![Docker Cloud Build Status Frontend](https://img.shields.io/docker/cloud/build/flaxo/frontend.svg?label=frontend)](https://hub.docker.com/r/flaxo/frontend)
[![Docker Cloud Build Status Backend](https://img.shields.io/docker/cloud/build/flaxo/backend.svg?label=backend)](https://hub.docker.com/r/flaxo/backend)
[![Docker Cloud Build Status data2graph](https://img.shields.io/docker/cloud/build/flaxo/data2graph.svg?label=data2graph)](https://hub.docker.com/r/flaxo/data2graph)

Flaxo is a complex system which consists of several independent services. 
Each one of them has its own docker image.

- backend server, [flaxo/backend](https://hub.docker.com/r/flaxo/backend)
- web client server, [flaxo/frontend](https://hub.docker.com/r/flaxo/frontend)
- data2graph tool, [flaxo/data2graph](https://hub.docker.com/r/flaxo/data2graph)

## Autobuilds

Flaxo docker hub organization is configured to automatically build Flaxo services. 
Each time a new version of the Flaxo platform is pushed to the repository then an automated build is triggered. 
New Flaxo version is basically a git tag starts with `v`. Some version examples are `v1`, `v1.2`, `v1.2.34`.
