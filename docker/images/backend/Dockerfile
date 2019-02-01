FROM ubuntu:18.04

ARG GITHUB_USER1_NAME
ARG GITHUB_USER1_TOKEN
ARG GITHUB_USER2_TOKEN
ARG GITHUB_USER3_TOKEN
ARG TRAVIS_USER1_TOKEN
ARG CODACY_USER1_TOKEN

ENV FLAXO_HOME=/opt/flaxo

# Git
RUN apt-get update \
    && apt-get install -y software-properties-common \
    && add-apt-repository -y ppa:git-core/ppa \
    && apt-get update \
    && apt-get install -y git

# Java 8
RUN apt-get update \
    && apt-get install -y openjdk-8-jre openjdk-8-jdk

# Travis cli
RUN apt-get update \
    && apt-get install -y ruby-full gcc libffi-dev make \
    && gem install travis -v 1.8.8

# Curl
RUN apt-get update \
    && apt-get install -y curl

# Flaxo
RUN mkdir -p $FLAXO_HOME \
    && cd $FLAXO_HOME \
    && git clone https://github.com/tcibinan/flaxo.git \
    && cp -r flaxo/* ./ \
    && rm -r flaxo \
    && ./gradlew rest:build

CMD cd $FLAXO_HOME && ./gradlew bootRun -x githubql:loadGithubScheme
