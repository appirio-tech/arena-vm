::Runs the TopCoder MPSQAS Applet from the command line.
::
:: Make sure to set your etc/hosts file with the proper IP for tc.cloud.topcoder.com

@echo off
setLocal EnableDelayedExpansion

SET CLASSPATH=.
FOR /f %%I IN ('dir /b *.jar') DO (SET CLASSPATH=!CLASSPATH!;%%I)

SET IP=@applet.host@
SET CLASSPATH=!CLASSPATH!
ECHO !CLASSPATH!
java -cp %CLASSPATH% com.topcoder.client.mpsqasApplet.LaunchMPSQAS %IP% @applet.port@
