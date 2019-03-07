#!/usr/bin/env bash

set -e

if [[ $# -eq 0 ]] ; then
    echo 'Flaxo image tag should be specified as an argument'
    exit 1
fi

tag=$1

cd backend
./build.sh $tag
cd ..

cd frontend
./build.sh $tag
cd ..
