#!/bin/bash

# build everything
APP_ROOT=~/dev/app
FARM_ROOT=~/dev/farm-server

cd $APP_ROOT
cp build.properties.vm build.properties
cp token.properties.vm token.properties
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

exit
