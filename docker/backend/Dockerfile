FROM ubuntu:18.04

ENV FLAXO_HOME=/opt/flaxo

# Git
RUN add-apt-repository -y ppa:git-core/ppa \
    && apt-get update \
    && apt-get -y install git

# Java 8
RUN apt-get update \
    && apt-get install -y openjdk-8-jre

# Travis cli
RUN apt-get update \
    && apt-get install -y ruby-full gcc libffi-dev make \
    && gem install travis -v 1.8.8

# Flaxo
RUN mkdir -p $FLAXO_HOME \
    && cd $FLAXO_HOME \
    && git clone https://github.com/tcibinan/flaxo.git \
    && cp flaxo/* ./ \
    && rm -r flaxo \
    && ./gradlew api:build

CMD ["cd", "$FLAXO_HOME", "&&", "./gradlew", "bootRun"]
