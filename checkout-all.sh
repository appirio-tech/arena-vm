#!/bin/bash

BRANCH=$1

if [ -z "$BRANCH" ]
then
  echo "branch name is required"
  exit 1
fi

echo Checking out branch $BRANCH

cd ~/dev
svn co https://coder.topcoder.com/internal/libs/encoder/$BRANCH libs/encoder
