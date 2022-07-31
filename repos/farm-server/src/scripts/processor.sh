# usage processor.sh <processor name>
echo "JAVA_OPTS=$JAVA_OPTS"
OS=`uname -o`
if [ "$OS" == "Cygwin" ]
then
  nohup java $JAVA_OPTS -classpath '..\conf;..\lib\*';$CORBA_CLASSPATH com.topcoder.farm.processor.ProcessorMain $1 &
else
  nohup java $JAVA_OPTS -classpath ../conf:../lib/*:$CORBA_CLASSPATH com.topcoder.farm.processor.ProcessorMain $1 &
fi

sleep 10