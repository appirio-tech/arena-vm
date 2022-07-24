#!/bin/bash

SSH_PRIV_KEY=$1

mkdir -p ~/.ssh/
echo "$SSH_PRIV_KEY" > ~/.ssh/id_rsa
chmod 600 ~/.ssh/id_rsa

ssh-keyscan github.com > ~/.ssh/known_hosts

rm -rf ~/dev && mkdir -p ~/dev

cd ~/dev

git clone git@github.com:appirio-tech/commons-aws.git
git clone git@github.com:appirio-tech/arena-vm.git

# checkout all repos
cd ~/dev/arena-vm
./checkout-all.sh dev

cd ~/dev/app
git checkout liuliquan

# fix files for docker
sed -i '10i <dependency org="commons-codec" name="commons-codec" rev="1.10" transitive="false"/>' ~/dev/farm-server/ivy.xml
sed -i '54i <dependency org="com.amazonaws" name="aws-java-sdk" rev="1.8.6"><artifact name="aws-java-sdk" type="jar"/></dependency>' ~/dev/farm-server/ivy.xml

sed -i 's/name="spring-context" rev="4.1.0.RELEASE"/name="spring-context" rev="5.3.22"/' ~/dev/app/ivy.xml

sed -i 's/@database.server@=localhost/@database.server@=arena-informix/' ~/dev/app/token.properties
sed -i 's/@database.dwserver@=localhost/@database.dwserver@=arena-informix/' ~/dev/app/token.properties
sed -i 's/@database.DWSERVER@=datawarehouse_tcp/@database.DWSERVER@=informixoltp_tcp/' ~/dev/app/token.properties
sed -i 's/@ldapHost@=localhost/@ldapHost@=arena-ldap/' ~/dev/app/token.properties
sed -i 's/@farm.mysql.host@=localhost/@farm.mysql.host@=arena-mysql/' ~/dev/app/token.properties

sed -i 's/ibiblio name="public" m2compatible="true"/ibiblio name="public" m2compatible="true" root="https:\/\/repo1.maven.org\/maven2"/' ~/dev/glue/settings/ivysettings-public.xml
sed -i 's/<ibiblio name="ibiblio" m2compatible="true" root="http:\/\/maven.appirio.net:8080" \/>//' ~/dev/glue/settings/ivysettings-default-chain.xml

sed -i 's/sqs.topcoder.com/localhost/' ~/dev/arena-vm/elasticmq-server.conf
sed -i 's/sqs.topcoder.com/localhost/' ~/dev/arena-vm/start-farm-processor.sh
sed -i 's/java -Dconfig.file=elasticmq-server.conf/nohup java -Dconfig.file=elasticmq-server.conf/' ~/dev/arena-vm/start-sqs-service.sh

echo "sleep 10" >> ~/dev/farm-server/src/scripts/processor.sh

# The docker/TC.cloud.ldap.keystore is created using TC_PROD_CA.pem from appiriodevops/ldap
cp ~/docker/TC.cloud.ldap.keystore ~/dev/arena-vm/TC.cloud.ldap.keystore

rm -rf ~/.ssh/
