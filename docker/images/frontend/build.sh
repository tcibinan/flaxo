set -e

if [[ $# -eq 0 ]] ; then
    echo 'Flaxo image tag should be specified as an argument'
    exit 1
fi

tag=$1

docker build . -t flaxo-frontend:$tag \
               --network=host

docker tag flaxo-frontend:$tag tcibinan/flaxo-frontend:$tag