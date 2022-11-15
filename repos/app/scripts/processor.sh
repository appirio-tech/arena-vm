CP=""
OS=`uname -o`

CONFIG_URL=""

if [ "$OS" == "Cygwin" ]
then
    CP=".\conf;.\lib\*"

    CONFIG_URL="file:///C:\\processor\\deploy\\conf\\config.xml"

	if [[ -z "${PROCESSOR_GROUP_ID}" ]] ; then
		echo "Use default windows group id: PR-WN"
		PROCESSOR_GROUP_ID="PR-WN"
	fi

	OSM=`uname -m`
	if [ "$OSM" == "x86_64" ] ; then
	    DOTNET_PLATFORM="x64"
	else
	    DOTNET_PLATFORM="x86"
	fi
	echo "DOTNET_PLATFORM=$DOTNET_PLATFORM"

	if [[ -z "${ILDASM}" ]] ; then
		ILDASM="C:\\Program Files\\Microsoft SDKs\\Windows\\v7.1\\Bin\\x64\\ildasm.exe"
	fi
	echo "ILDASM=$ILDASM"

	if [[ -z "${REF_DLLS}" ]] ; then
		REF_DLLS="System.Numerics.dll"
	fi
	echo "REF_DLLS=$REF_DLLS"
else
    CP="./conf:./lib/*"

    CONFIG_URL="file:///home/apps/processor/deploy/conf/config.xml"

	if [[ -z "${PROCESSOR_GROUP_ID}" ]] ; then
		echo "Use default linux group id: PR-LX"
		PROCESSOR_GROUP_ID="PR-LX"
	fi
	ILDASM=""
	REF_DLLS=""
	DOTNET_PLATFORM=""
fi

if [[ -z "${PROCESSOR_MAX_TASK_TIME}" ]] ; then
	PROCESSOR_MAX_TASK_TIME="850000"
fi

if [[ -z "${PROCESSOR_JAVA_OPTS}" ]] ; then
	PROCESSOR_JAVA_OPTS="-Xms1024m -Xmx2048m"
fi

if [[ -z "${PROCESSOR_EXTRA_EXECUTION_TIME}" ]] ; then
	PROCESSOR_EXTRA_EXECUTION_TIME="5000"
fi

echo "CONFIG_URL=$CONFIG_URL"
echo "PROCESSOR_GROUP_ID=$PROCESSOR_GROUP_ID"
echo "PROCESSOR_MAX_TASK_TIME=$PROCESSOR_MAX_TASK_TIME"
echo "PROCESSOR_EXTRA_EXECUTION_TIME=$PROCESSOR_EXTRA_EXECUTION_TIME"
echo "PROCESSOR_JAVA_OPTS=$PROCESSOR_JAVA_OPTS"

nohup java -cp $CP $PROCESSOR_JAVA_OPTS \
  -Dcom.topcoder.farm.type=processor \
  -Dcom.topcoder.farm.id=$PROCESSOR_GROUP_ID \
  -Dcom.topcoder.commandline.io=socket \
  -Dcom.topcoder.commandline.io.port=15968 \
  -Dconfiguration.xml.url=$CONFIG_URL \
  -DconfigurationProvider.class=com.topcoder.farm.processor.configuration.XMLConfigurationProvider \
  -Dcom.topcoder.services.tester.BaseTester.default_extra_execution_time=$PROCESSOR_EXTRA_EXECUTION_TIME \
  -Dcom.topcoder.services.tester.type.longtest.FarmLongTester.keepResultFolder=false \
  -Dcom.topcoder.services.tester.type.longtest.FarmLongTester.analyzeLog=false \
  -Dcom.topcoder.services.compiler.invoke.CPPCodeCompiler.srmCCNoThreadingOptions="g++ -std=c++17 -W -Wall -Wno-sign-compare -O2" \
  -Dcom.topcoder.services.compiler.invoke.CPPCodeCompiler.mmCCNoThreadingOptions="g++ -std=c++17 -W -Wall -Wno-sign-compare -O2" \
  -Dcom.topcoder.farm.processor.ProcessorInvocationRunner.maxTaskTime=$PROCESSOR_MAX_TASK_TIME \
  -Dcom.topcoder.services.compiler.util.dotnet.DotNetCompilerExecutor.platform=$DOTNET_PLATFORM \
  -Dcom.topcoder.services.compiler.util.dotnet.DotNetCompilerExecutor.refdll="$REF_DLLS" \
  -Dcom.topcoder.services.compiler.util.dotnet.DotNetSecurtyChecker.ildasm="$ILDASM" \
  com.topcoder.farm.processor.ProcessorMain $PROCESSOR_GROUP_ID &

echo "$!" > processor.pid
