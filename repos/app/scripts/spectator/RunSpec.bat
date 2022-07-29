@echo off
IF "%1"=="" (
	SET confloc=..\..\resources\scoreboard\Spectator.conf
) else (
	SET confloc=%1
)
IF EXIST %confloc% (
	ECHO Running configuration file located at %confloc%
) ELSE (
	ECHO Cannot find %confloc% -- running with default at ..\..\resources\scoreboard\Spectator.conf...
	SET confloc=..\..\resources\scoreboard\Spectator.conf
)
"%JAVA_HOME%\bin\java" -Xmx512M -Xms512M -Xincgc -Xshare:off ^
	-classpath ..\..\..\external-artifacts\log4j-1.2.13.jar;..\..\build\classes;..\..\images;..\..\resources ^
	-Dcom.topcoder.client.spectatorApp.EmulatePlasma=false ^
	com.topcoder.client.spectatorApp.SpectatorApp %confloc%
