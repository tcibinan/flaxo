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
    && apt-get install -y --no-install-recommends software-properties-common \
    && add-apt-repository -y ppa:git-core/ppa \
    && apt-get update \
    && apt-get install -y --no-install-recommends git \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Java 8
RUN apt-get update \
    && apt-get install -y --no-install-recommends openjdk-8-jre openjdk-8-jdk \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Travis cli
RUN apt-get update \
    && apt-get install -y --no-install-recommends ruby-full gcc libffi-dev make libc6-dev \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/* \
    && gem install travis -v 1.8.8

# Curl
RUN apt-get update \
     && apt-get install -y --no-install-recommends curl \
     && apt-get clean \
     && rm -rf /var/lib/apt/lists/*

# Flaxo
RUN git clone https://github.com/tcibinan/flaxo.git $FLAXO_HOME \
    && $FLAXO_HOME/gradlew rest:build -p $FLAXO_HOME

CMD $FLAXO_HOME/gradlew bootRun -x githubql:loadGithubScheme -p $FLAXO_HOME
