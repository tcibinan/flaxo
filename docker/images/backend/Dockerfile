# Build stage
FROM ubuntu:18.04 as builder

ENV FLAXO_HOME=/opt/flaxo

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
    && gem install travis -v 1.8.10

# Utils
RUN apt-get update \
     && apt-get install -y --no-install-recommends wget curl \
     && apt-get clean \
     && rm -rf /var/lib/apt/lists/*

# Flaxo
ARG GITHUB_USER1_NAME
ARG GITHUB_USER1_TOKEN
ARG GITHUB_USER2_TOKEN
ARG GITHUB_USER3_TOKEN
ARG TRAVIS_USER1_TOKEN
ARG CODACY_USER1_TOKEN
ARG VERSION

COPY ./ $FLAXO_HOME

RUN VERSION=$VERSION $FLAXO_HOME/gradlew rest:build -p $FLAXO_HOME

# Production stage
FROM ubuntu:18.04

ENV FLAXO_HOME=/opt/flaxo

WORKDIR $FLAXO_HOME

# Java 8
RUN apt-get update \
    && apt-get install -y --no-install-recommends openjdk-8-jre \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Travis cli
RUN apt-get update \
    && apt-get install -y --no-install-recommends ruby-full gcc libffi-dev make libc6-dev \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/* \
    && gem install travis -v 1.8.10

COPY --from=builder $FLAXO_HOME/rest/build/libs/rest.jar .

CMD java -jar $FLAXO_HOME/rest.jar
