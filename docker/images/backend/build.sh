docker build . -t flaxo-backend:latest \
               --network=host \
               --build-arg GITHUB_USER1_NAME=$GITHUB_USER1_NAME \
               --build-arg GITHUB_USER1_TOKEN=$GITHUB_USER1_TOKEN \
               --build-arg GITHUB_USER2_TOKEN=$GITHUB_USER2_TOKEN \
               --build-arg GITHUB_USER3_TOKEN=$GITHUB_USER3_TOKEN \
               --build-arg TRAVIS_USER1_TOKEN=$TRAVIS_USER1_TOKEN \
               --build-arg CODACY_USER1_TOKEN=$CODACY_USER1_TOKEN
