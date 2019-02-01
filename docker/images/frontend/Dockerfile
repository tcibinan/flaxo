FROM ubuntu:18.04

ENV FLAXO_HOME=/opt/flaxo
ENV NODEJS_HOME=/opt/nodejs

# Git
RUN apt-get update \
    && apt-get install -y software-properties-common \
    && add-apt-repository -y ppa:git-core/ppa \
    && apt-get update \
    && apt-get install -y git

# Java 8
RUN apt-get update \
    && apt-get install -y openjdk-8-jre openjdk-8-jdk

# Nodejs
RUN apt-get update \
    && apt-get install -y wget \
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
    && apt-get install -y python-minimal

# Flaxo
RUN . /etc/profile.d/nodejs.sh \
    && mkdir -p $FLAXO_HOME \
    && cd $FLAXO_HOME \
    && git clone https://github.com/tcibinan/flaxo.git \
    && cp -r flaxo/* ./ \
    && rm -r flaxo \
    && ./gradlew frontend:npm-install

CMD cd $FLAXO_HOME \
    && . /etc/profile.d/nodejs.sh \
    && ./gradlew runFrontend
