#!/bin/bash

URL="http://devnexusrepo.echoman.com:8080/repository"

REPO="artifacts"
USER="publish:publish"

group="com.echoman"
artifact="form-designehr"
version="$1"
groupPath=${group//.//}

cd ./application/target
ls -a

for file in *.zip; do
    curl -v \
      --write-out "\nStatus: %{http_code}\n" \
      -u $USER \
      --upload-file $file \
      -F "g=$group" \
      -F "a=$artifact" \
      -F "a=$version" \
      -F "p=zip" \
      $URL/$REPO/$groupPath/$artifact/$version/$file
done
