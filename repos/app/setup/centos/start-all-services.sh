WORKING_DIR=`pwd`

# Start tomcat
echo "starting tomcat"
cd /home/apps/tomcat/bin
sh startup.sh

# Start controller
echo "starting controller"
cd /home/apps/controller/launcher
rm -rf *.log nohup.out cache
sh start.sh

echo "waiting for controller"
sleep 40

# Start jboss
echo "starting jboss"
cd /home/apps/jboss/bin
sh start.sh

echo "waiting for jboss"

sleep 120

# Start listeners
echo "starting listeners"
cd /home/apps/app/scripts

rm -rf *.log nohup.out

sh runMainListener.sh
sh mpsqasListener.sh start 5016
sh runAdminListener.sh

echo "waiting for main listener..."

sleep 120

sh runWebSocketListener.sh

# Start processor
cd /home/apps/processor/launcher
rm -rf cache *.log nohup.out
sh start.sh

cd $WORKING_DIR

WORKING_DIR=

