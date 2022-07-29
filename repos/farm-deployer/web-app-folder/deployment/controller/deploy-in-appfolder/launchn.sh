#!/bin/bash
LAUNCH=$HOME/controller/launcher
CACHE=$LAUNCH/cache
DEPLOY=$HOME/controller/deploy

while :
do
    ID=$(cat $LAUNCH/idfile)
    LOGFILE=$LAUNCH/controller-`date +%Y-%m-%d-%H-%M-%S`.log
    URL=http\://farm-controller.topcoder.com\:8080/farm-deployer/launch.jnlp\?type=controller\&id=$ID

    # Load JNLP.
    mkdir -p $CACHE
    curl -s -o $CACHE/temp.jnlp $URL
    if [ $? -ne 0 ]
    then
        sleep 60
        continue;
    fi

    # Compare with last JNLP.
    cmp -s $CACHE/temp.jnlp $CACHE/last.jnlp
    CMP_RESULT=$?

    # Replace last JNLP with just loaded.
    cp -f $CACHE/temp.jnlp $CACHE/last.jnlp

    # If application not running now or JNLP got updated, then run the application based on last JNLP.
    PID=`cat $LAUNCH/farm.pid`
    pgrep -P $PID > /dev/null
    PGREP_RESULT=$?
    if [ $PGREP_RESULT -ne 0 ] || [ $CMP_RESULT -ne 0 ]
    then
        if [ $PGREP_RESULT -eq 0 ]
        then
            pkill -P $PID
        fi
        mkdir -p $DEPLOY
        cd $DEPLOY
        echo Executing JNLP.
        java -Dcom.sun.management.jmxremote -XX:+UseParallelGC -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -Xms256m -Xmx1538m -cp $LAUNCH/netx-0.5.2.jar netx.jnlp.runtime.Boot -verbose -basedir $LAUNCH -nosecurity -headless -jnlp file\:///$CACHE/last.jnlp >$LOGFILE 2>&1 &
    fi

    sleep 10
done
