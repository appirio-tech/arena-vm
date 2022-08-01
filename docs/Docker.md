## Local Docker Deployment

### Prerequisites

  - Docker

### Start Docker Containers

```bash
# Start docker containers
docker-compose up -d

# View docker logs, when you see below logs then containers are started succcessfully
# arena-mysql       | 2022-07-23T12:25:07.651734Z 0 [Note] mysqld: ready for connections.
# arena-ldap        | Starting slapd: [  OK  ]
# arena-informix    | *** Startup of informixoltp_tcp SUCCESS ***
# mock-tc-api       | Mock tc-api listen on port 8081
docker-compose logs -f
```



### Setup Informix

Use your favorite db tool (for example [DBeaver](https://dbeaver.io/)) to connect to Informix docker:

JDBC url is: `jdbc:informix-sqli://localhost:2021/informixoltp:INFORMIXSERVER=informixoltp_tcp`

```properties
Host=localhost
Port=2021
Server=informixoltp_tcp
Database=informixoltp
Username=informix
Password=1nf0rm1x
```

Then run following sql:

```sql
ALTER TABLE round_room_assignment ADD short_name varchar(100);
DROP TRIGGER trig_systemtest_modified;

UPDATE security_user SET password='4EjPjy6o+/C+dqNPnxIy9A==';

INSERT INTO informixoltp:informix.language (language_id, language_name, status, language_desc) VALUES(8, 'Python3', 'Y', '');

INSERT INTO informixoltp:informix.data_type_mapping (language_id, display_value, data_type_id) VALUES(8, 'integer', 1);
INSERT INTO informixoltp:informix.data_type_mapping (language_id, display_value, data_type_id) VALUES(8, 'float', 4);
INSERT INTO informixoltp:informix.data_type_mapping (language_id, display_value, data_type_id) VALUES(8, 'string (char)', 6);
INSERT INTO informixoltp:informix.data_type_mapping (language_id, display_value, data_type_id) VALUES(8, 'integer (byte)', 7);
INSERT INTO informixoltp:informix.data_type_mapping (language_id, display_value, data_type_id) VALUES(8, 'integer (short)', 13);
INSERT INTO informixoltp:informix.data_type_mapping (language_id, display_value, data_type_id) VALUES(8, 'long integer', 14);
INSERT INTO informixoltp:informix.data_type_mapping (language_id, display_value, data_type_id) VALUES(8, 'float', 15);
INSERT INTO informixoltp:informix.data_type_mapping (language_id, display_value, data_type_id) VALUES(8, 'string', 18);
INSERT INTO informixoltp:informix.data_type_mapping (language_id, display_value, data_type_id) VALUES(8, 'bool', 19);
INSERT INTO informixoltp:informix.data_type_mapping (language_id, display_value, data_type_id) VALUES(8, 'tuple (integer)', 20);
INSERT INTO informixoltp:informix.data_type_mapping (language_id, display_value, data_type_id) VALUES(8, 'tuple (float)', 21);
INSERT INTO informixoltp:informix.data_type_mapping (language_id, display_value, data_type_id) VALUES(8, 'tuple (string)', 22);
INSERT INTO informixoltp:informix.data_type_mapping (language_id, display_value, data_type_id) VALUES(8, 'tuple (long integer)', 24);
INSERT INTO informixoltp:informix.data_type_mapping (language_id, display_value, data_type_id) VALUES(8, 'tuple (tuple (integer))', 26);
INSERT INTO informixoltp:informix.data_type_mapping (language_id, display_value, data_type_id) VALUES(8, 'tuple (tuple (long integer))', 27);
INSERT INTO informixoltp:informix.data_type_mapping (language_id, display_value, data_type_id) VALUES(8, 'tuple (tuple (string))', 23);
INSERT INTO informixoltp:informix.data_type_mapping (language_id, display_value, data_type_id) VALUES(8, 'Matrix2D', 8);
```



### The server log files:

Server logs for `arena-app` container (`docker exec -it arena-app bash`): 

- JBOSS log: /home/apps/jboss-4.0.5.GA/server/default/log/server.log
- MPSQAS server log: /home/apps/app/scripts/mpsqasserver-<time>.log
- Admin server log: /home/apps/app/scripts/adminServer-<time>.log
- Arena server log: /home/apps/app/scripts/server-<time>.log
- WebSocket server log: /home/apps/app/scripts/webSocketServer-<time>.log
- Processor log: /home/apps/processor/deploy/bin/logs/processor.log
- nohup output: use `docker-compose logs -f arena-app`



### Verify Clients

At first add following to your hosts:

```
127.0.0.1 tc.cloud.topcoder.com
```

- Start mpsqas client:


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

- Start admin client:


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

- Start arena client:


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

### Start Dev Docker Containers

```bash
# Start docker containers
docker-compose -f docker-compose-dev.yml up -d
```

Note: 
  - `docker-compose-dev.yml` using ECSDockerfile which set `ENV ARENA_BUILD_TARGET=dev`, that means arena-app container use `token.properties.dev`, `build.properties.dev` and `applet.properties.dev` to build.
  - `ECSDockerfile` doesn't replace `sqs.topcoder.com` to `localhost` which is different from `Dockerfile`.