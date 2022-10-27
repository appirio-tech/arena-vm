#!/bin/bash


# argument 1 - start | stop | kill, depending on what you want to do.
# argument 2 - the port on which to start the admin listener.
# argument 3 - (optional) the ip and port at which the contest server is listening for admin connections,
#   in the format ip:port.  If unspecified, no contest server connection will be made.
#   This probably is not what you want.
# argument 4 - (optional) the port used by the logging server.  If unspecified, no logging server will be
#   instantiated.
JAVACMD=java
BASE=..
MAIN=com.topcoder.server.AdminListener.AdminListenerMain
PORT=6000
CMD=usage

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
CP=$CP:$LIBS/@filename.commons-lang3@
CP=$CP:$LIBS/@filename.commons-beanutils@
CP=$CP:$LIBS/@filename.commons-collections@
CP=$CP:$LIBS/@filename.commons-logging@
CP=$CP:$LIBS/@filename.jbossall-client@
CP=$CP:$LIBS/@filename.base_exception@
CP=$CP:$LIBS/@filename.basic_type_serialization@
CP=$CP:$CLASSPATH

if [[ $1 != "" ]] ; then
	CMD=$1
	shift
fi

if [[ $1 != "" ]] ; then
	PORT=$1
	PID_FILE=AdminListener.$PORT.pid
	shift
fi

if [[ $1 != "" ]] ; then
	CONTEST=$1
	shift
fi

if [[ -z "${ADMIN_LISTENER_JAVA_OPTS}" ]] ; then
	ADMIN_LISTENER_JAVA_OPTS="-Xms1024m -Xmx2048m"
fi

LOGGING_ID=AdminListener.$PORT
LOGGING_PROPERTY=com.topcoder.logging.id

if [ "$CMD" = "run" ] ; then
    $JAVACMD -cp $CP $ADMIN_LISTENER_JAVA_OPTS -D$LOGGING_PROPERTY=$LOGGING_ID $MAIN $PORT $CONTEST $@
elif [ "$CMD" = "start" ] ; then
    echo "nohup $JAVACMD -cp $CP $ADMIN_LISTENER_JAVA_OPTS -D$LOGGING_PROPERTY=$LOGGING_ID $MAIN $PORT $CONTEST $@ 2>&1 &"
    nohup $JAVACMD -cp $CP $ADMIN_LISTENER_JAVA_OPTS -D$LOGGING_PROPERTY=$LOGGING_ID $MAIN $PORT $CONTEST $@ 2>&1 &
    echo $! > $PID_FILE
	echo "start, port=$PORT, contest=$CONTEST, ADMIN_LISTENER_JAVA_OPTS=$ADMIN_LISTENER_JAVA_OPTS"
elif [ "$CMD" = "stop" ] ; then
    kill `cat $PID_FILE`
    rm -f $PID_FILE
	echo "stop, port=$PORT"
elif [ "$CMD" = "kill" ] ; then
    kill -3 `cat $PID_FILE`
    rm -f $PID_FILE
	echo "kill, port=$PORT"
else
    echo "Usage:"
    echo "adminListener.sh (run|start|stop|kill) [port] [contest listener IP:port] [logging port]"
    echo "	run   - start listener in the foreground"
    echo "	start - start listener in the background"
    echo "	stop  - stop listener"
    echo "	kill  - kill listener with 'kill -3'"
fi
