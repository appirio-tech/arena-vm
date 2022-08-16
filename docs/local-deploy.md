## Local Docker Deployment

This is to deploy in docker locally.

### Prerequisites

  - Docker

### Docker compose

[arena-vm/docker-compose.yml](../docker-compose.yml) is used to deploy in docker locally.

### Start Docker Containers

```bash
# Start docker containers
docker-compose up -d

# View docker logs, when you see below logs then containers are started succcessfully
# arena-mysql             | 2022-07-23T12:25:07.651734Z 0 [Note] mysqld: ready for connections.
# arena-ldap              | Starting slapd: [  OK  ]
# arena-informix          | *** Startup of informixoltp_tcp SUCCESS ***
# mock-tc-api             | Mock tc-api listen on port 8081
# arena-app               | Arena app startup complete
# arena-controller        | Arena Farm Controller startup complete
# arena-processor         | Arena Farm Processor startup complete
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



### The Container Logs:

Logs for `arena-app` container (`docker-compose exec arena-app bash`): 

- JBOSS log: /home/apps/jboss-4.0.5.GA/server/default/log/server.log
- MPSQAS Listener log: /home/apps/app/scripts/mpsqasserver-<time>.log
- Admin Listener log: /home/apps/app/scripts/adminServer-<time>.log
- Main Listener log: /home/apps/app/scripts/server-<time>.log
- WebSocket Listener log: /home/apps/app/scripts/webSocketServer-<time>.log
- Sysout log: `docker-compose logs -f arena-app`

Logs for `arena-controller` container (`docker-compose exec arena-controller bash`): 

- Processor log: /home/apps/controller/deploy/logs/controller-CT-MAIN.log
- Sysout log: `docker-compose logs -f arena-controller`

Logs for `arena-processor` container (`docker-compose exec arena-processor bash`): 

- Processor log: /home/apps/processor/deploy/logs/processor-<group>.log
- Sysout log: `docker-compose logs -f arena-processor`



### Verify Clients

At first add following to your hosts:

```
127.0.0.1 tc.cloud.topcoder.com
```

- Start mpsqas client:


```bash
# Copy built mpsqas client from docker
docker cp arena-app:/home/apps/dev/comp-eng/mpsqas-client/build/mpsqas-client-7.1.2.zip .

# Unzip
unzip mpsqas-client-7.1.2.zip -d mpsqas-client

# Run mpsqas client
cd mpsqas-client
sh mpsqas.sh

# Then login with lightspeed/password
```

- Start admin client:


```bash
# Copy built admin client from docker
docker cp arena-app:/home/apps/dev/app/dist/admin-client-7.1.6.zip .

# Unzip
unzip admin-client-7.1.6.zip -d admin-client

# Run admin client
cd admin-client
sh admin.sh

# Then login with heffan/password
```

- Start arena client:


```bash
# Copy built arena client from docker
docker cp arena-app:/home/apps/dev/comp-eng/arena-client/build/arena-client-7.1.4.zip .

# Unzip
unzip arena-client-7.1.4.zip -d arena-client

# Run arena client
cd arena-client
sh arena.sh

# Then login with twight/password
```



Refer to [ClientVerification.md](./ClientVerification.md) for verification details.



### Changed from base commits

arena-glue: 799de7605429a038093f8d1683aa16a0b99ad88c

mpsqas-client: f931dfb6e935adab5f50f96a5bcab78e678b4bee

arena-client: b5bdd3b840b980e30b6f3bb2831f86c4165e4803

app: 0e475f4f8aa16ade987099e564f9e7ff54b94fd0

arena-farm-client: a98b03dd5ff0b0a85d2d7a4d09c17bf1501dbe28

arena-farm-deployer: 102bed13e4999162edacaf3ed8a2cf653ed3bde8

arena-farm-server: 4031205f823478c5021404f17f9f87e9ae872f92

arena-farm-shared: 9c637b217a415c041adf989dedcb6d3daff969db

arena-shared: 1d3404e95c0ae8946e475fdf4d80292c2c004db7

arena-tc-shared: 83e8e4c17e06c9139304c4a102935e3c11ff92ab

compeng-common: 569787fa403d8c9ff9234a994aaa4ef0526be492

