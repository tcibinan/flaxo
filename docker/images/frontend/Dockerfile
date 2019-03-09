ENV FLAXO_HOME=/opt/flaxo

# Build stage
FROM ubuntu:18.04 as builder

ENV NODEJS_HOME=/opt/nodejs

# Java 8
RUN apt-get update \
    && apt-get install -y --no-install-recommends openjdk-8-jre openjdk-8-jdk \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Utils
RUN apt-get update \
    && apt-get install -y --no-install-recommends wget xz-utils \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Nodejs
RUN mkdir -p $NODEJS_HOME \
    && cd $NODEJS_HOME \
    && wget https://nodejs.org/dist/v10.15.1/node-v10.15.1-linux-x64.tar.xz \
    && tar xf node-v10.15.1-linux-x64.tar.xz \
    && cp -r node-v10.15.1-linux-x64/* ./ \
    && rm -r node-v10.15.1-linux-x64 \
    && echo "export NODEJS_HOME=$NODEJS_HOME" >> /etc/profile.d/nodejs.sh \
    && echo "export PATH=\$NODEJS_HOME/bin:\$PATH" >> /etc/profile.d/nodejs.sh \
    && chmod +x /etc/profile.d/nodejs.sh

# Flaxo
ARG TAG

RUN wget https://github.com/tcibinan/flaxo/archive/$TAG.tar.gz -O flaxo-$TAG.tar.gz \
    && tar -xzf /opt/flaxo-$TAG.tar.gz $FLAXO_HOME \
    && rm flaxo-$TAG.tar.gz \
    && $FLAXO_HOME/gradlew frontend:assembleFrontend -p $FLAXO_HOME

# Production stage
FROM ubuntu:18.04

WORKDIR $FLAXO_HOME

# Python
RUN apt-get update \
    && apt-get install -y --no-install-recommends python-minimal \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

COPY --from=builder $FLAXO_HOME/frontend/build/dist .

CMD python3 -m http.server
