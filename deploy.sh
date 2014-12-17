#!/bin/bash

ARENA_GIT_BRANCH=$1
ARENA_SVN_BRANCH=$2

# get the source
./checkout-all.sh $ARENA_GIT_BRANCH $ARENA_SVN_BRANCH

# build everything
APP_ROOT=~/dev/app
FARM_ROOT=~/dev/farm-server

cd $APP_ROOT
ant publish-workspace-all
ant compile-cpp2

cd ../comp-eng/arena-client
ant

cd ../mpsqas-client
ant package-applet

cd $APP_ROOT
ant package-app-deployment

cd $FARM_ROOT
ant package-processor-deployment

# deploy the files
cd
tar -xzvf $APP_ROOT/build/artifacts/osfiles.tgz

tar -xvzf $FARM_ROOT/build/artifacts/linux-osfiles.tgz

