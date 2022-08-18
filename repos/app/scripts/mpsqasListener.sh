#!/bin/bash
JAVACMD=java
BASE=../
MAIN=com.topcoder.server.mpsqas.listener.MPSQASListener
PORT=$2
PROCESSOR=DefaultProcessor
CMD=usage
PID_FILE=MPSQASListener.$PORT.pid

LIBS=$BASE/lib/jars
CP=$CP:$BASE/resources
CP=$CP:$LIBS/@filename.classes@
CP=$CP:$LIBS/@filename.arena-shared@
CP=$CP:$LIBS/@filename.client-common@
CP=$CP:$LIBS/@filename.client-socket@
CP=$CP:$LIBS/@filename.compeng-common@
CP=$CP:$LIBS/@filename.shared@
CP=$CP:$LIBS/@filename.concurrent@
CP=$CP:$LIBS/@filename.custom-serialization@
CP=$CP:$LIBS/@filename.encoder@
CP=$CP:$LIBS/@filename.farm-client@
CP=$CP:$LIBS/@filename.farm-deployer@
CP=$CP:$LIBS/@filename.farm-shared@
CP=$CP:$LIBS/@filename.http-tunnel-client@
CP=$CP:$LIBS/@filename.http-tunnel-server@
CP=$CP:$LIBS/@filename.logging@
CP=$CP:$LIBS/@filename.nbio-listener@
CP=$CP:$LIBS/@filename.security@
CP=$CP:$LIBS/@filename.log4j@
CP=$CP:$LIBS/@filename.commons-digester@
CP=$CP:$LIBS/@filename.activemq-core@
CP=$CP:$LIBS/@filename.activeio-core@
CP=$CP:$LIBS/@filename.backport-util-concurrent@
CP=$CP:$LIBS/@filename.jms@
CP=$CP:$LIBS/@filename.xpp3_min@
CP=$CP:$LIBS/@filename.commons-beanutils@
CP=$CP:$LIBS/@filename.commons-collections@
CP=$CP:$LIBS/@filename.commons-logging@
CP=$CP:$LIBS/@filename.jbossall-client@
CP=$CP:$LIBS/@filename.base_exception@
CP=$CP:$LIBS/@filename.basic_type_serialization@
CP=$CP:$CLASSPATH


LOGGING_ID=MPSQASListener.$PORT
LOGGING_PROPERTY=com.topcoder.logging.id

if [[ $1 != "" ]] ; then
    CMD=$1
    shift
fi

if [[ -z "${MPSQAS_LISTENER_JAVA_OPTS}" ]] ; then
	MPSQAS_LISTENER_JAVA_OPTS="-Xms1024m -Xmx2048m"
fi

if [ "$CMD" = "run" ] ; then
  $JAVACMD -cp $CP $MPSQAS_LISTENER_JAVA_OPTS  $MAIN $PORT
elif [ "$CMD" = "start" ] ; then
  echo "nohup $JAVACMD -cp $CP $MPSQAS_LISTENER_JAVA_OPTS -D$LOGGING_PROPERTY=$LOGGING_ID $MAIN $PORT 2>&1 &"
  nohup $JAVACMD -cp $CP $MPSQAS_LISTENER_JAVA_OPTS -D$LOGGING_PROPERTY=$LOGGING_ID $MAIN $PORT 2>&1 &
  echo $! > $PID_FILE
  echo "start, port=$PORT, MPSQAS_LISTENER_JAVA_OPTS=$MPSQAS_LISTENER_JAVA_OPTS"
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
