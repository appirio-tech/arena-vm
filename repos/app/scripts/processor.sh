CP=""
OS=`uname -o`

if [ "$OS" == "Cygwin" ]
then
    CP=".\conf;.\lib\*"

	if [[ -z "${PROCESSOR_GROUP_ID}" ]] ; then
		echo "Use default windows group id: PR-WN"
		PROCESSOR_GROUP_ID="PR-WN"
	fi
else
    CP="./conf:./lib/*"

	if [[ -z "${PROCESSOR_GROUP_ID}" ]] ; then
		echo "Use default linux group id: PR-LX"
		PROCESSOR_GROUP_ID="PR-LX"
	fi
fi

if [[ -z "${PROCESSOR_MAX_TASK_TIME}" ]] ; then
	echo "Use default max task time: 850000"
	PROCESSOR_MAX_TASK_TIME="850000"
fi

if [[ -z "${PROCESSOR_JAVA_OPTS}" ]] ; then
	PROCESSOR_JAVA_OPTS="-Xms1024m -Xmx2048m"
fi

echo "PROCESSOR_GROUP_ID=$PROCESSOR_GROUP_ID"
echo "PROCESSOR_MAX_TASK_TIME=$PROCESSOR_MAX_TASK_TIME"
echo "PROCESSOR_JAVA_OPTS=$PROCESSOR_JAVA_OPTS"

nohup java -cp $CP $PROCESSOR_JAVA_OPTS \
  -Dcom.topcoder.farm.type=processor \
  -Dcom.topcoder.farm.id=$PROCESSOR_GROUP_ID \
  -Dcom.topcoder.commandline.io=socket \
  -Dcom.topcoder.commandline.io.port=15968 \
  -Dconfiguration.xml.url=file:///home/apps/processor/deploy/conf/config.xml \
  -DconfigurationProvider.class=com.topcoder.farm.processor.configuration.XMLConfigurationProvider \
  -Dcom.topcoder.services.tester.BaseTester.default_extra_execution_time=5000 \
  -Dcom.topcoder.services.tester.type.longtest.FarmLongTester.keepResultFolder=false \
  -Dcom.topcoder.services.tester.type.longtest.FarmLongTester.analyzeLog=false \
  -Dcom.topcoder.services.compiler.invoke.CPPCodeCompiler.srmCCNoThreadingOptions="g++ -std=c++17 -W -Wall -Wno-sign-compare -O2" \
  -Dcom.topcoder.services.compiler.invoke.CPPCodeCompiler.mmCCNoThreadingOptions="g++ -std=c++17 -W -Wall -Wno-sign-compare -O2" \
  -Dcom.topcoder.farm.processor.ProcessorInvocationRunner.maxTaskTime=$PROCESSOR_MAX_TASK_TIME \
  com.topcoder.farm.processor.ProcessorMain $PROCESSOR_GROUP_ID &
