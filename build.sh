#!/bin/bash

if [ ! -d "repos" ]; then
  init="true"
  BRANCH="dev"
  mkdir repos
  cd repos
  echo Checking out branch $BRANCH
  git clone git@github.com:appirio-tech/arena-glue.git -b $BRANCH glue
  git clone git@github.com:appirio-tech/arena-encoder.git -b $BRANCH libs/encoder
  git clone git@github.com:appirio-tech/arena-logging.git -b $BRANCH libs/logging
  git clone git@github.com:appirio-tech/arena-client-socket.git -b $BRANCH libs/client-socket
  git clone git@github.com:appirio-tech/arena-nbio-listener.git -b $BRANCH libs/nbio-listener
  git clone git@github.com:appirio-tech/arena-custom-serialization.git -b $BRANCH libs/custom-serialization
  git clone git@github.com:appirio-tech/arena-concurrent.git -b $BRANCH libs/concurrent
  git clone git@github.com:appirio-tech/arena-http-tunnel-server.git -b $BRANCH libs/http-tunnel/server
  git clone git@github.com:appirio-tech/arena-http-tunnel-client.git -b $BRANCH libs/http-tunnel/client
  git clone git@github.com:appirio-tech/arena-farm-client.git -b $BRANCH farm-client
  git clone git@github.com:appirio-tech/arena-farm-deployer.git -b $BRANCH farm-deployer
  git clone git@github.com:appirio-tech/arena-farm-shared.git -b $BRANCH farm-shared
  git clone git@github.com:appirio-tech/arena-tc-shared.git -b $BRANCH shared
  git clone git@github.com:appirio-tech/arena-comp-eng-client-common.git -b $BRANCH comp-eng/client-common
  git clone git@github.com:appirio-tech/arena-farm-server.git -b $BRANCH farm-server
  git clone git@github.com:cloudspokes/arena-shared.git -b $BRANCH comp-eng/arena-shared
  git clone git@github.com:cloudspokes/arena-client.git -b $BRANCH comp-eng/arena-client
  git clone git@github.com:cloudspokes/mpsqas-client.git -b $BRANCH comp-eng/mpsqas-client
  git clone git@github.com:cloudspokes/compeng-common.git -b $BRANCH libs/compeng-common
  git clone git@github.com:cloudspokes/app.git -b $BRANCH app
  cd ..
  docker-compose up -d arena-informix arena-mysql
  echo "please fix repos config then run it again"
  exit
fi

# build everything

cd repos/app
ant clean-all clean-cache
ant publish-workspace-all
ant compile-cpp2

cd ../comp-eng/arena-client
ant

cd ../mpsqas-client
ant package-applet

cd ../../app
ant generate-farm-deployer
ant package-app-deployment


OS_BITS=`getconf LONG_BIT`
PROCESSOR_TYPE="-64bit"
if [ $OS_BITS -eq 32 ]; then
    PROCESSOR_TYPE=""
fi
cd ../farm-server
ant -DprocessorType=$PROCESSOR_TYPE package-processor-deployment

cd ../..
cp repos/app/build/artifacts/osfiles.tgz ./
cp repos/farm-server/build/artifacts/linux-osfiles.tgz ./

docker-compose up --build --force-recreate --no-deps -d arena-app

rm -rf osfiles.tgz linux-osfiles.tgz

echo "deploy completed"
