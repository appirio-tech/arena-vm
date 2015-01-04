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


OS_BITS=`getconf LONG_BIT`
PROCESSOR_TYPE="-64bit"
if [ $OS_BITS -eq 32 ]; then
    PROCESSOR_TYPE=""
fi

cd $FARM_ROOT
ant -DprocessorType=$PROCESSOR_TYPE package-processor-deployment


# deploy the files
cd
# clear all the old files
rm -rf /home/apps/app/controller /home/apps/app/lib /home/apps/app/resources /home/apps/app/scripts /home/apps/app/wrapper
tar -xzvf $APP_ROOT/build/artifacts/osfiles.tgz
cp /home/apps/dev/arena-vm/security.keystore.cloud /home/apps/app/scripts/

rm -rf rm -rf /home/apps/processor
tar -xvzf $FARM_ROOT/build/artifacts/linux-osfiles.tgz

exit
