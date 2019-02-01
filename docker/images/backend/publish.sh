set -e

if [[ $# -eq 0 ]] ; then
    echo 'Flaxo image tag should be specified as an argument'
    exit 1
fi

tag=$1

docker push tcibinan/flaxo-backend:$tag
