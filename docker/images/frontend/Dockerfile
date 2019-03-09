FROM ubuntu:18.04

ENV FLAXO_HOME=/opt/flaxo
ENV NODEJS_HOME=/opt/nodejs

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

# Nodejs
RUN apt-get update \
    && apt-get install -y --no-install-recommends wget xz-utils \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/* \
    && mkdir -p $NODEJS_HOME \
    && cd $NODEJS_HOME \
    && wget https://nodejs.org/dist/v10.15.1/node-v10.15.1-linux-x64.tar.xz \
    && tar xf node-v10.15.1-linux-x64.tar.xz \
    && cp -r node-v10.15.1-linux-x64/* ./ \
    && rm -r node-v10.15.1-linux-x64 \
    && echo "export NODEJS_HOME=$NODEJS_HOME" >> /etc/profile.d/nodejs.sh \
    && echo "export PATH=\$NODEJS_HOME/bin:\$PATH" >> /etc/profile.d/nodejs.sh \
    && chmod +x /etc/profile.d/nodejs.sh

# Python 2
RUN apt-get update \
    && apt-get install -y --no-install-recommends python-minimal \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Flaxo
ARG TAG

RUN git clone https://github.com/tcibinan/flaxo.git $FLAXO_HOME \
    && git checkout $TAG \
    && . /etc/profile.d/nodejs.sh \
    && $FLAXO_HOME/gradlew frontend:npm-install -p $FLAXO_HOME

CMD . /etc/profile.d/nodejs.sh \
    && $FLAXO_HOME/gradlew runFrontend -x githubql:loadGithubScheme -p $FLAXO_HOME
