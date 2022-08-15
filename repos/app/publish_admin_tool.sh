#       Admin Java Web Start Setup Script
#       Version 1.0.0
#       Last Update: 15 Nov,2014
#       by TCSASSEMBLER
#
#
#       1.0.0 - Initial Script
#
#       This scripts will properly unpack the built admin client zip files
#       and deploy to /mnt/apache/contest/.
#

APACHE_DIR="/mnt/apache/contest"
KEYSTORE="/home/apps/dev/farm-deployer/web-app-folder/farm.keystore"
ZIP_FILE="/home/apps/dev/app/dist/admin-client-*.zip"
ZIP_FILE_NAME=`find $ZIP_FILE`
TARGET_DIR="$APACHE_DIR/classes/admintool7"
JNLP_DIR="$APACHE_DIR/arena"

# Fail and exit the execution
function die_on_error() {
    echo "Execute failed, aborted."
    exit 1
}

echo "Starting setup for Admin JNLP..."
mkdir -p $APACHE_DIR || die_on_error

if [[ -f $ZIP_FILE_NAME ]]; then
    rm -rf $TARGET_DIR
    mkdir -p $TARGET_DIR
    echo "Unzip client files.."
    unzip -q -u $ZIP_FILE_NAME -d $TARGET_DIR 
    echo "Moving JNLP files..."
    mkdir -p  $JNLP_DIR
    mv $TARGET_DIR/*.jnlp $JNLP_DIR/
    echo "Signing client jars..."
    for i in `ls -1 $TARGET_DIR/*.jar`; do
        jarsigner -J-Xmx512m -keystore $KEYSTORE -storepass changeit $i deployer
    done;
else
    echo "$ZIP_FILE does not exist!"
fi

echo "Done with setup for Admin JNLP."


