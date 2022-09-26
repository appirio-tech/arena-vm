CLASSPATH=.
for i in `ls -1 ./*.jar`; do
     CLASSPATH=$CLASSPATH:$i
done;

IP=@applet.arg-hostname@
java -Dcom.topcoder.message.LoggingInterceptor=true -cp $CLASSPATH com.topcoder.client.contestApplet.runner.generic $IP @applet.arg-port@ https://$IP\:5008/dummy\?t\=true TopCoder TopCoder
