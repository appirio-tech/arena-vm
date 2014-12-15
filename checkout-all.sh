#!/bin/bash

BRANCH=$1

if [ -z "$BRANCH" ]
then
  echo 'usage: checkout-all.sh <git branch> <svn branch>'
  exit 1
fi

SVN_BRANCH=$2
if [ -z "$SVN_BRANCH" ]
then
  echo 'usage: checkout-all.sh <git branch> <svn branch>'
  exit 2
fi

echo Checking out branch $SVN_BRANCH

cd ~/dev
svn co https://coder.topcoder.com/internal/libs/encoder/$SVN_BRANCH libs/encoder
svn co https://coder.topcoder.com/internal/libs/logging/$SVN_BRANCH libs/logging
svn co https://coder.topcoder.com/internal/libs/nbio-listener/$SVN_BRANCH libs/nbio-listener
svn co https://coder.topcoder.com/internal/libs/custom-serialization/$SVN_BRANCH libs/custom-serialization
svn co https://coder.topcoder.com/internal/libs/concurrent/$SVN_BRANCH libs/concurrent
svn co https://coder.topcoder.com/internal/libs/http-tunnel/server/$SVN_BRANCH libs/http-tunnel/server
svn co https://coder.topcoder.com/internal/libs/http-tunnel/client/$SVN_BRANCH libs/http-tunnel/client
svn co https://coder.topcoder.com/internal/farm-client/$SVN_BRANCH farm-client
svn co https://coder.topcoder.com/internal/farm-deployer/$SVN_BRANCH farm-deployer
svn co https://coder.topcoder.com/internal/farm-shared/$SVN_BRANCH farm-shared
svn co https://coder.topcoder.com/internal/shared/$SVN_BRANCH shared
svn co https://coder.topcoder.com/internal/comp-eng/client-common/$SVN_BRANCH comp-eng/client-common

echo Checking out branch $BRANCH

# unique git hostnames are needed for multiple github deploy keys - see ~/.ssh/config
git clone git@git-farm-server:appirio-tech/arena-farm-server.git -b $BRANCH farm-server
git clone git@github.com:cloudspokes/arena-shared.git -b $BRANCH comp-eng/arena-shared
git clone git@github.com:cloudspokes/arena-client.git -b $BRANCH comp-eng/arena-client
git clone git@github.com:cloudspokes/compeng-common.git -b $BRANCH libs/compeng-common
git clone git@github.com:cloudspokes/app.git -b $BRANCH app

