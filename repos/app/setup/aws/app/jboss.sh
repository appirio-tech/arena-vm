#!/bin/bash

echo nohup java ${JAVA_OPTS} ${JBOSS_JAVA_OPTS} -Djava.endorsed.dirs=$JBOSS_HOME/lib/endorsed -classpath $JBOSS_HOME/bin/run.jar:$JBOSS_HOME/lib/tools.jar org.jboss.Main 
nohup java ${JAVA_OPTS} ${JBOSS_JAVA_OPTS} -Djava.endorsed.dirs=$JBOSS_HOME/lib/endorsed -classpath $JBOSS_HOME/bin/run.jar:$JBOSS_HOME/lib/tools.jar org.jboss.Main &

exit
