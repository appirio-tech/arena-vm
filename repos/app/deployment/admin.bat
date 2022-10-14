::Runs the TopCoder Arena Applet from the command line.
::
:: Make sure to set your etc/hosts file with the proper IP for tc.cloud.topcoder.com

@echo off
setLocal EnableDelayedExpansion

SET CLASSPATH=.
FOR /f %%I IN ('dir /b *.jar') DO (SET CLASSPATH=!CLASSPATH!;%%I)

SET JAVACMD=java
SET MAIN=com.topcoder.client.contestMonitor.view.gui.MonitorGUIMain
SET IP=@applet.arg-hostname@
SET PORT=@applet.arg-port@
SET CLASSPATH=!CLASSPATH!
ECHO !CLASSPATH!

%JAVACMD% -cp %CLASSPATH% %MAIN% %IP%:%PORT%
