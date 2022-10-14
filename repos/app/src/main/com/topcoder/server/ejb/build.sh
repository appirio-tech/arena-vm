directoryname=$1
classname=$1
jarname=$1

echo 'Creating EJB for '$directoryname

#. $WL_HOME/setEnv.sh
WL_HOME=/export/home/weblog5/weblogic

MYCLASSPATH=$CLASSPATH:/usr/java/jdk1.3.1/jre/lib/rt.jar

# Create the build directory, and copy the deployment descriptors into it
cd $directoryname

#remove existing build directory 
rm -f *.class
rm -rf build

#create new build directory and copy deployment descriptors to it
echo 'building directory and coping deployment descriptors...'
mkdir -p build build/META-INF
cp -f ejb-jar.xml weblogic-ejb-jar.xml weblogic-cmp-rdbms-jar.xml build/META-INF

#compile java files
echo 'Compiling...'
javac -d build -classpath $MYCLASSPATH *.java


# Make a standard ejb jar file, including XML deployment descriptors
echo 'Creating jar...'
(cd build; jar cv0f "$jarname".jar META-INF ejb)

# Run ejbc to create the deployable jar file
rm -f $CPP_HOME/app/build/ejb_jars/"$jarname".jar 
echo 'Running ejbc...'
echo $BEAN_HOME
java -classpath $MYCLASSPATH \
   -Dweblogic.home=$WL_HOME weblogic.ejbc \
    -compiler javac build/"$jarname".jar \
    $CPP_HOME/app/build/ejb_jars/"$jarname".jar 

echo

