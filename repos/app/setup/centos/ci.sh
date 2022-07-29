WORKING_PATH=`pwd`

sh stop-all-services.sh

cd $WORKING_PATH

sh rebuild.sh

cd  $WORKING_PATH

sh start-all-services.sh

cd $WORKING_PATH
