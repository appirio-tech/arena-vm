# usage processor.sh <processor name>
OS=`uname -o`
if [ "$OS" == "Cygwin" ]
then
  echo "nohup java $JAVA_OPTS -classpath '..\conf;..\lib\*';$CP com.topcoder.farm.processor.ProcessorMain $1 &"
  nohup java $JAVA_OPTS -classpath '..\conf;..\lib\*';$CP com.topcoder.farm.processor.ProcessorMain $1 &
else
  echo "nohup java $JAVA_OPTS -classpath ../conf:../lib/*:$CP com.topcoder.farm.processor.ProcessorMain $1 &"
  nohup java $JAVA_OPTS -classpath ../conf:../lib/*:$CP com.topcoder.farm.processor.ProcessorMain $1 &
fi
