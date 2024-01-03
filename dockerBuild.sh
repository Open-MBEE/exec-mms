#!/bin/bash

if [[ -z $1 ]]; then
    ORG="iasartifact.sncorp.intranet.com:8443/ias-mbse-local-docker-dev"
else
    ORG=$1
fi
VERSION=$(cat gradle.properties | grep version= | awk -F= '{printf $2}')
docker buildx build . -t $ORG/open-mbee/mms:$VERSION --progress=plain --load

docker push $ORG/open-mbee/mms:$VERSION