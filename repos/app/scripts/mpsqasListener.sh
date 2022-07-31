#!/bin/bash
JAVACMD=java
BASE=../
MAIN=com.topcoder.server.mpsqas.listener.MPSQASListener
PORT=$2
PROCESSOR=DefaultProcessor
MAXMEM=1024m
LOGFILE=mpsqasserver-`date +%Y-%m-%d-%H-%M-%S`.log
CMD=usage
PID_FILE=mpsqasListener$PORT.pid

LIBS=$BASE/lib/jars
CP=$CP:$BASE/resources
CP=$CP:$LIBS/*
CP=$CP:$CLASSPATH

if [[ $1 != "" ]] ; then
    CMD=$1
    shift
fi

if [ "$CMD" = "run" ] ; then
  $JAVACMD -cp $CP -Xmx$MAXMEM  $MAIN $PORT
elif [ "$CMD" = "start" ] ; then
  nohup $JAVACMD -cp $CP -Xmx$MAXMEM  $MAIN $PORT > ./$LOGFILE 2>&1 &
  echo $! > $PID_FILE
	echo "start port=$PORT"
elif [ "$CMD" = "stop" ] ; then
  kill `cat $PID_FILE`
  rm -f $PID_FILE
	echo "stop port=$PORT"
elif [ "$CMD" = "kill" ] ; then
  kill -3 `cat $PID_FILE`
  rm -f $PID_FILE
	echo "kill port=$PORT"
else
  echo "Usage:"
  echo "./mpsqasListener.sh (run|start|stop|kill) [port]"
  echo "        run    - start listener in the foreground"
  echo "        start  - start listener in the background"
  echo "        stop   - stop background listener" 
  echo "        kill   - kill listener with 'kill -3'"
fi 
