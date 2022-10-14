WORKING_DIR=`pwd`

echo "stopping services"

cd /home/apps/processor/launcher
sh stop.sh
rm -rf cache *.log nohup.out

cd /home/apps/app/scripts

sh stopWebSocketListener.sh
sh stopAdminListener.sh
sh mpsqasListener.sh stop 5016
sh stopMainListener.sh

rm -rf *.log nohup.out

echo "stopping jboss"
jps | grep " Main" | awk '{print -9,$1}' |xargs kill

echo "stopping controller"
cd ~/controller/launcher
sh stop.sh
rm -rf *.log nohup.out cache

echo "stopping tomcat"
cd /home/apps/tomcat/bin
sh shutdown.sh

cd $WORKING_DIR

WORKING_DIR=

