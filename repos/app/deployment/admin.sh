CLASSPATH=.
for i in `ls -1 ./*.jar`; do
     CLASSPATH=$CLASSPATH:$i
done;

IP=@applet.arg-hostname@
#java -cp $CLASSPATH com.topcoder.client.contestMonitor.view.gui.MonitorGUIMain $IP:5003
java -Djava.security.properties=custom.security -cp $CLASSPATH com.topcoder.client.contestMonitor.view.gui.MonitorGUIMain $IP:@applet.arg-port@
