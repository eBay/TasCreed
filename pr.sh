#!/bin/bash

if [ $# -lt 1 ] ;then
    echo "you need input the number of PR you want to pull"
    exit 1;
fi

NUM=${1}

if ! [[ "$NUM" =~ ^[1-9][0-9]*$ ]] ;then
    echo "only integer is the correct parameter"
    exit 1;
fi

echo "try to fetch PR-$NUM"

if [ -n "$(git status --porcelain)" ]; then
  echo "unstable branch, not ready to fetch a PR branch, need commit or stash"
  exit 1;
else
  echo "stable branch, ready to fetch PR-$NUM"
fi

git fetch origin pull/${NUM}/head:PR-${NUM}

echo "fetch PR-$NUM success, switch to branch PR-$NUM"

git checkout PR-${NUM}