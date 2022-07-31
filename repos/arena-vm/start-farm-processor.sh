#!/bin/bash
rm -rf ~/processor/work
mkdir ~/processor/work
rm -rf ~/processor/cache
mkdir ~/processor/cache
chmod +x ~/processor/deploy/app/cpp/timeout/timeout


OS_BITS=`getconf LONG_BIT`
CPP_ARGUMENTS="-DcppArguments=-std=c++17"
if [ $OS_BITS -eq 32 ]; then
    CPP_ARGUMENTS=""
fi

# sqs.topcoder.com:9324 = elasticmq (remote sqs server)
export JAVA_OPTS="$CPP_ARGUMENTS -Darena.sqs-endpoint=http://sqs.topcoder.com:9324 -Darena.env-prefix=dev -Daws.accessKeyId=x -Daws.secretKey=x"

cd ~/processor/deploy/bin
echo Starting processor...
./processor.sh PR-LX

echo Startup complete
exit
