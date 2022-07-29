#!/bin/bash
# Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
#
# This script will automatically setup environment under root account.
#
# Usage:
#    chmod +x env_setup.sh
#    ./env_setup.sh  or sudo ./env_setup.sh
#
# @version 1.1
# @author TCSASSEMBLER
# changes in 1.1
# - md5 check
# - no tmp directory. It will use current directory as working directory.

WGET_JDK_URL="https://dl.dropboxusercontent.com/s/6h8fqmf413zfj5m/jdk-6u45-linux-x64.bin?dl=1"
WGET_JDK_FILE="jdk-6u45-linux-x64.bin"
WGET_JDK_MD5="40c1a87563c5c6a90a0ed6994615befe"
WGET_ANT_URL="http://archive.apache.org/dist/ant/binaries/apache-ant-1.7.0-bin.tar.gz"
WGET_ANT_FILE="apache-ant-1.7.0-bin.tar.gz"
WGET_ANT_MD5="d721b0add40355d7049ec694b91eb332"
WGET_ANT_CONTRIB_URL="http://sourceforge.net/projects/ant-contrib/files/ant-contrib/ant-contrib-1.0b2/ant-contrib-1.0b2-bin.tar.gz/download"
WGET_ANT_CONTRIB_FILE="ant-contrib-1.0b2-bin.tar.gz"
WGET_ANT_CONTRIB_MD5="a5e185c04ab3937edfeb02e2c6f6fc2b"

function check_prerequisites() {
    echo "Check permissions..."
    if [[ $UID -ne 0 ]]; then
        echo "Check permissions failed: This script must be run as a sudoer."
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
    FILE=$1
    URL=$2
    MD5=$3
    COUNTER=0

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
    echo "Installing JDK..."
    download_file $WGET_JDK_FILE $WGET_JDK_URL $WGET_JDK_MD5
    chmod +x $WGET_JDK_FILE
    ./$WGET_JDK_FILE || die_on_error
    mv jdk1.6.0_45 /usr/

    echo "Installing Apache Ant..."
    download_file $WGET_ANT_FILE $WGET_ANT_URL $WGET_ANT_MD5
    tar zxf $WGET_ANT_FILE || die_on_error
    mv apache-ant-1.7.0 /opt/

    echo "Install Apache Ant Contrib..."
    download_file $WGET_ANT_CONTRIB_FILE $WGET_ANT_CONTRIB_URL $WGET_ANT_CONTRIB_MD5
    tar zxf $WGET_ANT_CONTRIB_FILE || die_on_error
    cp -f ant-contrib/lib/ant-contrib.jar /opt/apache-ant-1.7.0/lib
}

function clean_install_dirs() {
    rm -rf jdk1.6.0_45 > /dev/null 2>&1
    rm -rf apache-ant-1.7.0 > /dev/null 2>&1
    rm -rf ant-contrib > /dev/null 2>&1
    rm -rf /usr/jdk1.6.0_45 > /dev/null 2>&1
    rm -rf /opt/apache-ant-1.7.0 > /dev/null 2>&1
}

function main() {

    check_prerequisites

    # Install some basic commands, which should have been already installed, 
    # but run this command for sure
    yum install -y wget

    # Install common utilities, which are not installed by default in CentOS 6.4
    yum install -y svn git

    # For farm processor
    yum install -y gcc gcc-c++

    echo "Clean previous installation directories..."
    clean_install_dirs

    echo "Install componets..."
    install_components
    
    echo "Done. Environment is setup successfully"
}

main
