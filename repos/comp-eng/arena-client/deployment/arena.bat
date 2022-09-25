::Runs the TopCoder Arena Applet from the command line.
::
:: Make sure to set your etc/hosts file with the proper IP for tc.cloud.topcoder.com

@echo off
setLocal EnableDelayedExpansion

SET CLASSPATH=.
FOR /f %%I IN ('dir /b *.jar') DO (SET CLASSPATH=!CLASSPATH!;%%I)

SET IP=tc.cloud.topcoder.com
SET PORT=5001
SET CLASSPATH=!CLASSPATH!
ECHO !CLASSPATH!

java -Dcom.topcoder.message.LoggingInterceptor=true -cp %CLASSPATH% com.topcoder.client.contestApplet.runner.generic %IP% %PORT% https://%IP%\:5008/dummy\?t\=true TopCoder TopCoder
