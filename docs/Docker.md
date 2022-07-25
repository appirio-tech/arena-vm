## Local Docker Deployment

### Prerequisites

  - Docker
  - [Github account with SSH key configured](https://docs.github.com/en/authentication/connecting-to-github-with-ssh/adding-a-new-ssh-key-to-your-github-account)

### Start Docker Containers

```bash
# Goto docker folder
cd docker

# Start docker containers
docker-compose up -d

# View docker logs, when you see below logs then containers are started succcessfully
# arena-mysql       | 2022-07-23T12:25:07.651734Z 0 [Note] mysqld: ready for connections.
# arena-ldap        | Starting slapd: [  OK  ]
# arena-informix    | *** Startup of informixoltp_tcp SUCCESS ***
docker-compose logs -f
```



The informix docker is a bit outdated, need fix db schema:

```bash
# Login to arena-informix containter
docker exec -it arena-informix bash

# Fix db schema
echo "ALTER TABLE round_room_assignment ADD short_name varchar(100)" | dbaccess informixoltp@informixoltp_tcp

# Exit from arena-informix containter
exit
```



### Build & Start Arena Services

```bash
# Checkout Github repos 
# Note your Github account need be configured with SSH key
# Replace "/path-to-ssh-private-key" to your actual path of SSH private key
docker exec -it arena-app bash /home/apps/docker/checkout.sh "$(cat /path-to-ssh-private-key)"

# Build arena services, will take several minutes
docker exec -it arena-app bash /home/apps/docker/build.sh

# Start arena services, will take several minutes, when you see "Startup Complete" then it's finished
docker exec -it arena-app bash -c "cd /home/apps/dev/arena-vm && ./start-services.sh"
```



The server log files:

- JBOSS log: /home/apps/jboss-4.0.5.GA/server/default/log/server.log
- MPSQAS server log: /home/apps/app/scripts/mpsqasserver-<time>.log
- Admin server log: /home/apps/app/scripts/adminServer-<time>.log
- Arena server log: /home/apps/app/scripts/server-<time>.log
- WebSocket server log: /home/apps/app/scripts/webSocketServer-<time>.log
- nohup output:
  - /home/apps/app/scripts/nohup.out
  - /home/apps/processor/deploy/bin/nohup.out



### Verify Clients

At first add `127.0.0.1 tc.cloud.topcoder.com` to your hosts.

Start mpsqas client:

```bash
# Copy built mpsqas client from docker
docker cp arena-app:/home/apps/dev/comp-eng/mpsqas-client/build/mpsqas-client-7.1.1.zip .

# Unzip
unzip mpsqas-client-7.1.1.zip -d mpsqas-client

# Run mpsqas client
cd mpsqas-client
sh mpsqas.sh

# Then login with lightspeed/password
```

Start admin client:

```bash
# Copy built admin client from docker
docker cp arena-app:/home/apps/dev/app/dist/admin-client-7.1.5.zip .

# Unzip
unzip admin-client-7.1.5.zip -d admin-client

# Run admin client
cd admin-client
sh admin.sh

# Then login with heffan/password
```

Start arena client:

```bash
# Copy built arena client from docker
docker cp arena-app:/home/apps/dev/comp-eng/arena-client/build/arena-client-7.1.3.zip .

# Unzip
unzip arena-client-7.1.3.zip -d arena-client

# Run arena client
cd arena-client
sh arena.sh

# Then login with twight/password
```



Refer to [ClientVerification.md](./ClientVerification.md) for verification details.



**NOTE: the latest built arena client has error when login to TopCoder production server, this is due to [UserInfo class is changed](https://github.com/appirio-tech/compeng-common/commit/e4e1939b5362c8af04cb218a784692a0e66ba298)  by adding an `admin4Web` field, but not yet deployed in production. So when arena client [reads the UserInfo](https://github.com/appirio-tech/compeng-common/blob/dev/src/main/com/topcoder/netCommon/contestantMessages/UserInfo.java#L208) object, it expects the `admin4Web` field, but is not sent from production server.**



### Files changed

Refer to `docker/checkout.sh` for the file changes:

- `app/ivy.xml`:
  - change `spring-context` version from `4.1.0.RELEASE` to `5.3.22`
- `app/token.properties`:
  - change informix host to docker container name `arena-informix`
  - change mysql host to docker container name `arena-mysql`
  - change ldap host to docker container name `arena-ldap`
  - change `datawarehouse_tcp` to `informixoltp_tcp` regard to informix docker
- `farm-server/ivy.xml`:
  - add `commons-codec` and `aws-java-sdk` dependencies
- `farm-server/src/scripts/processor.sh`:
  - add `sleep 10` to wait for processor to start
- `glue/settings/ivysettings-public.xml`:
  - set public root to `https://repo1.maven.org/maven2` 
- `glue/settings/ivysettings-default-chain.xml`:
  - remove `<ibiblio name="ibiblio" m2compatible="true" root="http://maven.appirio.net:8080" />` line
- `arena-vm/elasticmq-server.conf`:
  - change `sqs.topcoder.com` to `localhost`
- `arena-vm/start-farm-processor.sh`:
  - change `sqs.topcoder.com` to `localhost`
- `arena-vm/start-sqs-service.sh`:
  - use `nohup` to run `elasticmq-server`
- `arena-vm/TC.cloud.ldap.keystore`:
  - created using `TC_PROD_CA.pem` from `appiriodevops/ldap` docker image

