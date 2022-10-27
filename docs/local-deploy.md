## Local Docker Deployment

This is to deploy in docker locally.

### Prerequisites

  - Docker
  - Cygwin and .NET Framework for Windows processor

### Docker compose

[arena-vm/docker-compose.yml](../docker-compose.yml) is used to deploy in docker locally.

### Start Docker Containers

```bash
# Start informix/mysql/ldap/mock-tc-api
docker-compose up -d arena-informix arena-mysql arena-ldap mock-tc-api
docker-compose logs -f
# Wait following docker logs then containers are started succcessfully
# arena-mysql             | 2022-07-23T12:25:07.651734Z 0 [Note] mysqld: ready for connections.
# arena-ldap              | Starting slapd: [  OK  ]
# arena-informix          | *** Startup of informixoltp_tcp SUCCESS ***
# mock-tc-api             | Mock tc-api listen on port 8081

# Start arena-controller
docker-compose up -d arena-controller
docker-compose logs -f arena-controller
# Wait following log then arena-controller is started succcessfully
# arena-controller        | Arena Farm Controller startup complete

# Start arena-processor
docker-compose up -d arena-processor
docker-compose logs -f arena-processor
# Wait following log then arena-processor is started succcessfully
# arena-processor         | Arena Farm Processor startup complete

# Start arena-jboss
docker-compose up -d arena-jboss
docker-compose logs -f arena-jboss
# Wait following log then arena-jboss is started succcessfully
# arena-jboss             | Arena JBoss startup complete

# Start arena-listeners
docker-compose up -d arena-listeners
docker-compose logs -f arena-listeners
# Wait following log then arena-listeners is started succcessfully
# arena-listeners         | Arena Listeners startup complete

# Start arena-websocket
docker-compose up -d arena-websocket
docker-compose logs -f arena-websocket
# Wait following log then arena-websocket is started succcessfully
# arena-websocket         | WebSocket Server startup complete

# Start arena-applets
docker-compose up -d arena-applets
docker-compose logs -f arena-applets
# Wait following log then arena-applets is started succcessfully
# arena-applets           | Applets Clients startup complete

# Start arena-nginx
docker-compose up -d arena-nginx
docker-compose logs -f arena-nginx
# Wait following log then arena-nginx is started succcessfully
# arena-nginx             | [notice] 8#8: start worker processes
```



### Start Farm Processor on Windows

- Install Cygwin on Windows: https://www.cygwin.com/install.html. Assuming it's installed on `C:\cygwin64`.

- Install .NET Framework on Windows: https://dotnet.microsoft.com/en-us/download/dotnet-framework/thank-you/net48-developer-pack-offline-installer.

- Find the folder path of `csc` and `vbc` commands and add it to `Path` environment variable.

  Find the path of `ildasm.exe` and set it to `ILDASM` environment variable.

  For example, after install .NET Framwork 4.8 on Windows 10:

  ```
  set Path=C:\Windows\Microsoft.NET\Framework64\v4.0.30319;%Path%
  set ILDASM=C:\Program Files (x86)\Microsoft SDKs\Windows\v10.0A\bin\NETFX 4.8 Tools\ildasm.exe
  ```

- Download the `processor-windows.tgz` from docker container:

  ```bash
  docker cp arena-listeners:/home/apps/dev/app/dist/processor-windows.tgz .
  ```

  Then copy `processor-windows.tgz` to Windows and extract it to`C:` drive.

  The extracted path should be `C:\processor\deploy`.

- Start Windows processor:

  ```
  C:\cygwin64\bin\bash.exe --login -c "cd /cygdrive/c/processor/deploy && ./processor.sh"
  ```



### Setup Mysql

Use your favorite db tool (for example [DBeaver](https://dbeaver.io/)) to connect to Mysql docker:

JDBC url is: `jdbc:mysql://localhost:3306/farm`

```properties
Host=localhost
Port=3306
Database=farm
Username=farm
Password=farmpass
```

Then run following sql:

```sql
update FARM_PROC_PROPERTIES_MAP as p, 
(select PRO_ID from FARM_PROC_PROPERTIES_MAP where PROPERTY_VALUE = '<string>linux</string>') as linux_id
set p.PROPERTY_VALUE = '<set><int>1</int><int>3</int><int>6</int><int>9</int></set>' 
where p.PROPERTY_NAME LIKE '%languages'
and p.PRO_ID = linux_id.PRO_ID;
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
DROP TRIGGER trig_systemtest_modified;

UPDATE security_user SET password='4EjPjy6o+/C+dqNPnxIy9A==';

INSERT INTO informixoltp:informix.language (language_id, language_name, status, language_desc) VALUES(9, 'Python3', 'Y', '');

INSERT INTO informixoltp:informix.data_type_mapping (language_id, display_value, data_type_id) VALUES(9, 'integer', 1);
INSERT INTO informixoltp:informix.data_type_mapping (language_id, display_value, data_type_id) VALUES(9, 'float', 4);
INSERT INTO informixoltp:informix.data_type_mapping (language_id, display_value, data_type_id) VALUES(9, 'string (char)', 6);
INSERT INTO informixoltp:informix.data_type_mapping (language_id, display_value, data_type_id) VALUES(9, 'integer (byte)', 7);
INSERT INTO informixoltp:informix.data_type_mapping (language_id, display_value, data_type_id) VALUES(9, 'integer (short)', 13);
INSERT INTO informixoltp:informix.data_type_mapping (language_id, display_value, data_type_id) VALUES(9, 'long integer', 14);
INSERT INTO informixoltp:informix.data_type_mapping (language_id, display_value, data_type_id) VALUES(9, 'float', 15);
INSERT INTO informixoltp:informix.data_type_mapping (language_id, display_value, data_type_id) VALUES(9, 'string', 18);
INSERT INTO informixoltp:informix.data_type_mapping (language_id, display_value, data_type_id) VALUES(9, 'bool', 19);
INSERT INTO informixoltp:informix.data_type_mapping (language_id, display_value, data_type_id) VALUES(9, 'tuple (integer)', 20);
INSERT INTO informixoltp:informix.data_type_mapping (language_id, display_value, data_type_id) VALUES(9, 'tuple (float)', 21);
INSERT INTO informixoltp:informix.data_type_mapping (language_id, display_value, data_type_id) VALUES(9, 'tuple (string)', 22);
INSERT INTO informixoltp:informix.data_type_mapping (language_id, display_value, data_type_id) VALUES(9, 'tuple (long integer)', 24);
INSERT INTO informixoltp:informix.data_type_mapping (language_id, display_value, data_type_id) VALUES(9, 'tuple (tuple (integer))', 26);
INSERT INTO informixoltp:informix.data_type_mapping (language_id, display_value, data_type_id) VALUES(9, 'tuple (tuple (long integer))', 27);
INSERT INTO informixoltp:informix.data_type_mapping (language_id, display_value, data_type_id) VALUES(9, 'tuple (tuple (string))', 23);
INSERT INTO informixoltp:informix.data_type_mapping (language_id, display_value, data_type_id) VALUES(9, 'Matrix2D', 8);
```



### The Container Logs:

Logs for `arena-jboss` container: `docker-compose logs -f arena-jboss`

Logs for `arena-listeners` container: `docker-compose logs -f arena-listeners`

Logs for `arena-websocket` container:  `docker-compose logs -f arena-websocket`

Logs for `arena-controller` container:  `docker-compose logs -f arena-controller`

Logs for `arena-processor` container:  `docker-compose logs -f arena-processor`

Logs for Windows processor: `C:\processor\deploy\nohup.out`

### Verify Clients

At first add following to your hosts:

```
127.0.0.1 tc.cloud.topcoder.com
```

The cert [../nginx-ssl/server.crt](../nginx-ssl/server.crt) is self signed, need import it to JDK cacerts:

```bash
# To import the self signed cert to JDK cacerts
keytool -importcert -noprompt -storepass changeit -alias arena-local-crt -file ./nginx-ssl/server.crt -cacerts

# To delete the self signed cert from JDK cacerts
keytool -delete -noprompt -storepass changeit -alias arena-local-crt -cacerts
```

Copy built applets from docker:

```bash
docker cp arena-applets:/home/apps/applets.zip .
unzip applets.zip -d applets
cd applets
```

- Start mpsqas client:


```bash
# Run mpsqas client
sh mpsqas.sh

# Then login with lightspeed/password
```

- Start admin client:


```bash
# Run admin client
sh admin.sh

# Then login with heffan/password
```

- Start arena client:


```bash
# Run arena client
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

