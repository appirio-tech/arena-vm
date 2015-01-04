#!/bin/bash

# clear existing source
./delete-dev-source.sh

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


OS_BITS=`getconf LONG_BIT`
PROCESSOR_TYPE="-64bit"
if [ $OS_BITS -eq 32 ]; then
    PROCESSOR_TYPE=""
fi
cd $FARM_ROOT
ant -DprocessorType=$PROCESSOR_TYPE package-processor-deployment

# deploy the files
cd
rm -rf /home/apps/app/controller /home/apps/app/lib /home/apps/app/resources /home/apps/app/scripts /home/apps/app/wrapper
tar -xzvf $APP_ROOT/build/artifacts/osfiles.tgz

rm -rf rm -rf /home/apps/processor
tar -xvzf $FARM_ROOT/build/artifacts/linux-osfiles.tgz

# start arena services
cd ~/dev/arena-vm
./start-services.sh

exit
