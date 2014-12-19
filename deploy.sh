#!/bin/bash

ARENA_GIT_BRANCH=`get-vm-param arena-git-branch`
ARENA_SVN_BRANCH=`get-vm-param arena-svn-branch`

if [ -z "$ARENA_GIT_BRANCH" ]
then
  ARENA_GIT_BRANCH=dev
fi

if [ -z "$ARENA_SVN_BRANCH" ]
then
  ARENA_SVN_BRANCH=trunk
fi

# get the source
./checkout-all.sh $ARENA_GIT_BRANCH $ARENA_SVN_BRANCH

# build everything
APP_ROOT=~/dev/app
FARM_ROOT=~/dev/farm-server

cd $APP_ROOT
ant clean-all clean-cache
ant publish-workspace-all
ant compile-cpp2

cd ../comp-eng/arena-client
ant

cd ../mpsqas-client
ant package-applet

cd $APP_ROOT
ant generate-farm-deployer
ant package-app-deployment

cd $FARM_ROOT
ant package-processor-deployment

# deploy the files
cd
tar -xzvf $APP_ROOT/build/artifacts/osfiles.tgz

tar -xvzf $FARM_ROOT/build/artifacts/linux-osfiles.tgz

# start arena services
cd ~/dev/arena-vm
./start-services.sh

exit
