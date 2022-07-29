function checkNewSvnVersion() {
  SVN_CURRENT_REVISION=`svn info |grep Revision: |cut -c11-`
  svn update
  SVN_NEW_REVISION=`svn info |grep Revision: |cut -c11-`

  if [ "$SVN_CURRENT_REVISION" -ne "$SVN_NEW_REVISION" ]; then
    BUILD_REQUIRED=1
  fi  
}

function checkNewGitVersion() {
  git fetch -a
  NEW_VERSION_MSG=`git status | grep behind`

  if [ -n "$NEW_VERSION_MSG" ]; then
    BUILD_REQUIRED=1
  fi
}

function main() {
  CURRENT_WORKING_DIR=`pwd`  

  BUILD_REQUIRED=0  
  
  cd ~/dev/app
  checkNewGitVersion
  
  cd ~/dev/comp-eng/arena-client
  checkNewGitVersion

  cd ~/dev/comp-eng/arena-shared
  checkNewGitVersion

  cd ~/dev/comp-eng/client-common
  checkNewSvnVersion

  cd ~/dev/comp-eng/fraud
  checkNewSvnVersion

  cd ~/dev/comp-eng/mpsqas-client
  checkNewGitVersion

  cd ~/dev/farm-client
  checkNewSvnVersion

  cd ~/dev/farm-deployer
  checkNewSvnVersion

  cd ~/dev/farm-server
  checkNewSvnVersion

  cd ~/dev/farm-shared
  checkNewSvnVersion

  cd ~/dev/glue
  checkNewSvnVersion

  cd ~/dev/libs/client-socket
  checkNewSvnVersion

  cd ~/dev/libs/compeng-common
  checkNewGitVersion

  cd ~/dev/libs/concurrent
  checkNewSvnVersion

  cd ~/dev/libs/custom-serialization
  checkNewSvnVersion

  cd ~/dev/libs/encoder
  checkNewSvnVersion

  cd ~/dev/libs/http-tunnel/client
  checkNewSvnVersion

  cd ~/dev/libs/http-tunnel/server
  checkNewSvnVersion

  cd ~/dev/libs/logging
  checkNewSvnVersion

  cd ~/dev/libs/nbio-listener
  checkNewSvnVersion

  cd ~/dev/shared
  checkNewSvnVersion

  if [ "$BUILD_REQUIRED" -eq "1" ]; then
    echo "a new build is required" 
    cd $CURRENT_WORKING_DIR
    ./arena_setup.sh
    ./restart_services.sh
  fi

  cd $CURRENT_WORKING_DIR
}

main

