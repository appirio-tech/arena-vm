#!/bin/bash
# Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
#
# This script will automatically setup all Arena services under the same account.
#
# Usage:
#    chmod +x arena_setup.sh
#    ./arena_setup.sh
#
# @version 1.1
# @author TCSASSEMBLER
# changes in 1.1
# - md5 check
# - No tmp directory. It will use current directory as working directory.

WGET_JBOSS_URL="http://sourceforge.net/projects/jboss/files/JBoss/JBoss-4.0.5.GA/jboss-4.0.5.GA.zip/download"
WGET_JBOSS_FILE="jboss-4.0.5.GA.zip"
WGET_JBOSS_MD5="a39e85981958fea2411e9346e218aa39"
WGET_TOMCAT_URL="http://archive.apache.org/dist/tomcat/tomcat-5/v5.5.27/bin/apache-tomcat-5.5.27.zip"
WGET_TOMCAT_FILE="apache-tomcat-5.5.27.zip"
WGET_TOMCAT_MD5="795f55ae86cfc78d0c13ecf3b48515ac"

WORKING_PATH=`pwd`

function check_prerequisites() {
    echo "Check directory..."
    if [ ! -f arena_setup.sh ] ; then
        echo "Check directory failed: This script must be run from its directory."
        exit 1
    fi
}

function file_check() {
    FILE=$1
    MD5=$2
    if [[ -f $FILE ]]; then
        echo "File is already exist"
        echo "Checking md5..."
        md5sum=`md5sum $FILE | cut -d ' ' -f1`
        if [[ "$MD5" = "$md5sum" ]]; then
            echo "md5 is matched"
            result=0    # download is not required
            return
        else
            echo "md5 is not matched. Delete file"
            rm -rf $FILE > /dev/null 2>&1
            result=1    # download is required
            return
        fi
    else
        result=1        # download is required
        return
    fi
}

function download_file() {
    URL=$1
    FILE=$2
    MD5=$3

    file_check $FILE $MD5
    if [ "$result" -eq "0" ]; then
        echo "Already exist, skip downloading"
        return
    fi

    wget -O $FILE $URL
    file_check $FILE $MD5
    
    if [ "$result" -eq "1" ]; then
        echo "Downloading $FILE failed, aborted."
        exit 1
    else
        echo "Downloading $FILE succeed."
    fi
}

function die_on_error() {
    echo "Executed failed, aborted."
    exit 1
}

function install_components() {
    echo "Install JBoss..."
    download_file $WGET_JBOSS_URL $WGET_JBOSS_FILE $WGET_JBOSS_MD5
    unzip -qo $WGET_JBOSS_FILE
    mv jboss-4.0.5.GA ~/
    ln -s ~/jboss-4.0.5.GA ~/jboss

    echo "Install Tomcat..."
    download_file $WGET_TOMCAT_URL $WGET_TOMCAT_FILE $WGET_TOMCAT_MD5
    unzip -qo $WGET_TOMCAT_FILE
    mv apache-tomcat-5.5.27 ~/
    ln -s ~/apache-tomcat-5.5.27 ~/tomcat
    chmod +x ~/tomcat/bin/*.sh
}

function setup_services() {
    echo "Setup controller..."
    cd ~/dev/app
    ant setup-controller

    echo "Setup farm processor..."
    ant -f build_ci.xml setup-processor
    
    echo "Setup deployer..."
    cd ~/dev/farm-deployer
    # mv tokens.properties.ci tokens.properties
    ant war
    cp ~/dev/farm-deployer/build/farm-deployer.war ~/tomcat/webapps

    # Back to script directory
    cd $WORKING_PATH
}

function checkout_all() {
    # Make directory and checkout all
    rm -rf ~/dev
    mkdir -p ~/dev
    cd ~/dev
    svn co https://coder.topcoder.com/internal/glue/trunk glue --username=vm-web --password=kasdf92348 --no-auth-cache
    cd glue
    ant checkout -Dproject=client-socket
    ant checkout -Dproject=farm-client
    ant checkout -Dproject=farm-deployer
    ant checkout -Dproject=farm-shared
    ant checkout -Dproject=farm-server
    ant checkout -Dproject=http-tunnel-client
    ant checkout -Dproject=http-tunnel-sever
    ant checkout -Dproject=nbio-listener
    ant checkout -Dproject=shared
    ant checkout -Dproject=custom-serialization
    ant checkout -Dproject=concurrent
    ant checkout -Dproject=logging
    ant checkout -Dproject=encoder
    ant checkout -Dproject=fraud    
    ant checkout -Dproject=client-common
    ant clone -Dproject=mpsqas-client
    ant clone -Dproject=compeng-common
    ant clone -Dproject=app    
    ant clone -Dproject=arena-client
    ant clone -Dproject=arena-shared

    cd $WORKING_PATH
}

function build_application() {
    # Copy config to overwrite
    cp ~/app/token.properties.ci.ec2 ~/dev/app/token.properties
    cp ~/dev/app/build.properties.ci ~/dev/app/build.properties

    cd ~/dev/app/
    ant -f build_ci.xml clean clean-all clean-cache
    ant -f build_ci.xml publish-workspace-all
    ant -f build_ci.xml deploy-app deploy-jboss update-farm-deployer

    cd $WORKING_PATH
}

function build_clients() {
    cd ~/dev/comp-eng/arena-client
    ant publish-workspace-all package-applet

    cd ~/dev/comp-eng/mpsqas-client
    ant publish-workspace-all package-applet

    cd ~/dev/app
    ant -f build_ci.xml publish-workspace-all package-AdminTool

    cd $WORKING_PATH
}

function main() {
    
    echo "Checkout all..."
    checkout_all

    echo "Build application..."
    build_application

    echo "Build clients..."
    build_clients
}

main
