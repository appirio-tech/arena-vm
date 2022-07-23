# build commons-aws lib
cd ~/dev/commons-aws
git checkout 360b256
mvn clean package -DskipTests=true
mkdir -p ~/.ivy2/.ivy-cache
cp ~/dev/commons-aws/target/commons-aws-0.0.1-SNAPSHOT.jar ~/.ivy2/.ivy-cache/

rm -rf /home/apps/app
rm -rf /home/apps/processor

# build everything
APP_ROOT=~/dev/app
FARM_ROOT=~/dev/farm-server

cd $APP_ROOT
ant clean-all clean-cache
ant publish-workspace-all
ant compile-cpp2
ant package-AdminTool

cd ../comp-eng/arena-client
ant

cd ../mpsqas-client
ant package-applet

cd $APP_ROOT
ant package-app-deployment

OS_BITS=`getconf LONG_BIT`
PROCESSOR_TYPE="-64bit"
if [ $OS_BITS -eq 32 ]; then
    PROCESSOR_TYPE=""
fi
cd $FARM_ROOT
ant -DprocessorType=$PROCESSOR_TYPE package-processor-deployment

# deploy the files
cd ~
tar -xzvf $APP_ROOT/build/artifacts/osfiles.tgz
tar -xvzf $FARM_ROOT/build/artifacts/linux-osfiles.tgz

cp -r /home/apps/app/jboss-4.0.5.GA /home/apps/