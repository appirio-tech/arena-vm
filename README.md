arena-vm
========

Initialization scripts for the Arena VM

## Local Deployment

### pre-requisites

  1. jdk8
  2. ant 1.7.1
  3. docker

### deploy

  1. instanll commons-aws
    * pull `git@github.com:appirio-tech/commons-aws.git` repo and checkout `360b256` commit. If you don't have this repo
    * run `mvn clean package -DskipTests=true` to build package
    * run `cp target/commons-aws-0.0.1-SNAPSHOT.jar ~/.ivy2/.ivy-cache/` move the package for ivy build. If you don't have this folder, you should create it first.
  2. set env `BUILD_COMPILE_SOURCE=1.8`
  3. run `./build.sh` to pull all dependencies project and create containers.

### verify

  1. add `127.0.0.1       tc.cloud.topcoder.com` to your hosts
  2. run `ant package-AdminTool` in `repos/app` folder
  3. navigate to `dist` foler and run `unzip admin-client-7.1.5.zip -d admin-client`
  4. navigate to `admin-client` and run `sh admin.sh`
