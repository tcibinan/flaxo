FROM ubuntu:18.04

ENV DATA2GRAPH_HOME=/opt/data2graph
ENV NODEJS_HOME=/opt/nodejs

# Git
RUN add-apt-repository -y ppa:git-core/ppa \
    && apt-get update \
    && apt-get -y install git

# Java 8
RUN apt-get update \
    && apt-get install -y openjdk-8-jre

# Nodejs
RUN mkdir -p $NODEJS_HOME \
    && cd $NODEJS_HOME \
    && wget https://nodejs.org/dist/v10.15.1/node-v10.15.1-linux-x64.tar.xz \
    && tar -xzf node-v10.15.1-linux-x64.tar.xz \
    && cp node-v10.15.1-linux-x64/* ./ \
    && rm -r node-v10.15.1-linux-x64 \
    && echo  "export NODEJS_HOME=/opt/nodejs/bin \
              export PATH=\$NODEJS_HOME:\$PATH" \
              > /etc/profile.d/nodejs.sh \
    && . /etc/profile.d/nodejs.sh

# Data2graph
RUN mkdir -p $DATA2GRAPH_HOME \
    && cp $DATA2GRAPH_HOME \
    && git https://github.com/tcibinan/graph2viz.git \
    && cp graph2viz/* ./ \
    && rm -r graph2viz \
    && ./gradlew build

CMD ["cd", "$DATA2GRAPH_HOME", "&&", "./gradlew", "run"]
