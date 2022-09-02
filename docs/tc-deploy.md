## TC Docker Deployment

This is about how to deploy in docker in Topcoder environments.

### Prerequisites

  - Docker
  - Cygwin and .NET Framework for Windows processor

### Setup Mysql

Run following sql to add Python3 lanuage to linux processors:

```sql
update FARM_PROC_PROPERTIES_MAP as p, 
(select PRO_ID from FARM_PROC_PROPERTIES_MAP where PROPERTY_VALUE = '<string>linux</string>') as linux_id
set p.PROPERTY_VALUE = '<set><int>1</int><int>3</int><int>6</int><int>8</int></set>' 
where p.PROPERTY_NAME LIKE '%languages'
and p.PRO_ID = linux_id.PRO_ID;
```

### Setup Informix

Run following sql to add Python3 language:

```sql
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



### Build Docker Image

The docker image is built using token file [arena-vm/repos/app/token.properties.dev](../repos/app/token.properties.dev) for dev env.

Similarly, when built for prod env, token file `arena-vm/repos/app/token.properties.prod` will be used.

#### Informix tokens

These are informix related tokens. Change theses according to TC environment:

```properties
# The database related configurations
@database.server@=arena-informix
@database.port@=2021
@database.username@=informix
@database.password@=1nf0rm1x
@database.INFORMIXSERVER@=informixoltp_tcp
```

#### Mysql tokens

These are mysql related tokens. Change theses according to TC environment:

```properties
@farm.mysql.host@=arena-mysql
@farm.mysql.port@=3306
@farm.mysql.username@=farm
@farm.mysql.password@=farmpass
```

#### LDAP tokens

These are LDAP related tokens. Change theses according to TC environment:

```properties
## Tokens used in LDAP.properties
@ldapHost@=arena-ldap
@ldapPort@=389
```

#### Arena host tokens

These are arena host related tokens. Change theses according to TC environment:

```properties
# The arena-jboss should be the host where JBoss deployed
@contestHostUrl@=arena-jboss:1299
@jmsHostUrl@=arena-jboss:1299
@hostUrl@=arena-jboss:1299
@pactsHostUrl@=192.168.12.777:1099
@ejbServerUrl@=arena-jboss:1299
@jmsServerUrl@=arena-jboss:1299
@jbossEJBServerUrl@=arena-jboss:1299
@topicJMSConnString@=tcp://arena-jboss:8293

# The arena-controller should be the host whether Farm Controller deployed
@farmHost@=arena-controller
@farmControllerAppHost@=arena-controller

# The arena-listeners should be the host whether Listeners deployed
@arenaListernerAppHost@=arena-listeners
```

#### SSO tokens

These are SSO related tokens. Change theses according to TC environment:

```properties
@ssoCookieKey@=tcsso
@ssoHashSecret@=Yb2oMAtUoJyl6LvHEnRYF1Q5u5ags7DN6PXgDPZAU9Ku68k7wTJHIDaIT0DjXYubam
@ssoDomain@=topcoder-dev.com
@jwtCookieKey@=tcjwt
```



### Security Related Files

There are some security related files in `arena-vm/repos/app` folder, you need change them for dev/prod env before build image.

For LDAP:

- repos/app/scripts/TC.cloud.ldap.keystore
- repos/app/resources/LDAP.properties

For password encryption:

- repos/app/scripts/security.keystore.cloud
- repos/app/resources/com/topcoder/security/Util.properties



### Container Environment Variables

Environment variables for Farm Controller:

| Environment Variable | Description                               | Default Value       |
| -------------------- | ----------------------------------------- | ------------------- |
| CONTROLLER_JAVA_OPTS | The opts to start controller Java process | -Xms1024m -Xmx2048m |

Environment variables for Farm Processor:

| Environment Variable    | Description                              | Default Value       |
| ----------------------- | ---------------------------------------- | ------------------- |
| PROCESSOR_JAVA_OPTS     | The opts to start processor Java process | -Xms1024m -Xmx2048m |
| PROCESSOR_GROUP_ID      | The group name the processor belongs to  | PR-LX               |
| PROCESSOR_MAX_TASK_TIME | Max task execution time in milliseconds  | 850000              |

And for Windows processor to handle C#/VB languages, there are addition env variables supported:

| Environment Variable | Description                 | Default Value                                                |
| -------------------- | --------------------------- | ------------------------------------------------------------ |
| ILDASM               | The path to ildasm.exe      | C:\\Program Files\\Microsoft SDKs\\Windows\\v7.1\\Bin\\x64\\ildasm.exe |
| REF_DLLS             | The assembly reference dlls | System.Numerics.dll                                          |

Environment variables for JBoss:

| Environment Variable    | Description                                   | Default Value       |
| ----------------------- | --------------------------------------------- | ------------------- |
| JBOSS_JAVA_OPTS         | The opts to start JBoss Java process          | -Xms2048m -Xmx8192m |
| JBOSS_STARTUP_WAIT_TIME | The time to wait for JBoss startup in seconds | 120                 |

Environment variables for Listeners:

| Environment Variable             | Description                                           | Default Value       |
| -------------------------------- | ----------------------------------------------------- | ------------------- |
| MAIN_LISTENER_JAVA_OPTS          | The opts to start Main Listener Java process          | -Xms1024m -Xmx2048m |
| ADMIN_LISTENER_JAVA_OPTS         | The opts to start Admin Listener Java process         | -Xms1024m -Xmx2048m |
| MPSQAS_LISTENER_JAVA_OPTS        | The opts to start MPSQAS Listener Java process        | -Xms1024m -Xmx2048m |
| WEBSOCKET_LISTENER_JAVA_OPTS     | The opts to start WebSocket Listener Java process     | -Xms1024m -Xmx2048m |
| MAIN_LISTENRER_STARTUP_WAIT_TIME | The time to wait for Main Listener startup in seconds | 120                 |

### Start Docker Containers

- Start container for Farm Controller:

  ```yaml
    arena-controller-dev:
      image: "arena-app:dev"
      command: ["/home/apps/start-services.sh", "controller"]
      ports:
        - "25000:25000"
        - "25001:25001"
      environment:
        - CONTROLLER_JAVA_OPTS=-Xms256m -Xmx1538m
  ```

  

- Start container for Farm Processor:

  ```yaml
    arena-processor-dev:
      image: "arena-app:dev"
      depends_on:
        - arena-controller-dev
      command: ["/home/apps/start-services.sh", "processor"]
      environment:
        - PROCESSOR_JAVA_OPTS=-Xms256m -Xmx1538m
        - PROCESSOR_GROUP_ID=PR-LX
        - PROCESSOR_MAX_TASK_TIME=850000
  ```

  

- Start container for JBoss:

  ```yaml
    arena-jboss-dev:
      image: "arena-app:dev"
      depends_on:
        - arena-controller-dev
      command: ["/home/apps/start-services.sh", "jboss"]
      ports:
        - "1299:1299"
        - "8293:8293"
      environment:
        - JBOSS_JAVA_OPTS=-Xms512m -Xmx2048m
        - JBOSS_STARTUP_WAIT_TIME=30
  ```

  

- Start container for Listeners:

  ````yaml
    arena-listeners-dev:
      image: "arena-app:dev"
      depends_on:
        - arena-jboss-dev
        - arena-controller-dev
      command: ["/home/apps/start-services.sh", "listeners"]
      ports:
        - "5001:5001"
        - "5003:5003"
        - "5008:5008"
        - "5037:5037"
        - "5555:5555"
      environment:
        - MAIN_LISTENER_JAVA_OPTS=-Xms256m -Xmx1024m
        - ADMIN_LISTENER_JAVA_OPTS=-Xms256m -Xmx1024m
        - MPSQAS_LISTENER_JAVA_OPTS=-Xms256m -Xmx1024m
        - WEBSOCKET_LISTENER_JAVA_OPTS=-Xms256m -Xmx1024m
        - MAIN_LISTENRER_STARTUP_WAIT_TIME=60
  ````

  

- Start container for Websocket:

  ```yaml
    arena-websocket-dev:
      image: "arena-app:dev"
      depends_on:
        - arena-jboss-dev
        - arena-controller-dev
      command: ["/home/apps/start-services.sh", "websocket"]
      ports:
        - "7443:7443"
      environment:
        - WEBSOCKET_LISTENER_JAVA_OPTS=-Xms256m -Xmx1024m
  ```

  

### Start Multiple Processors

At first refer to [ArenaArchitecture.md](./ArenaArchitecture.md) about the processor groups.

Assume you have 2 groups defined: `Group-One` and `Group-Two`, then you can start 2 processors:

```yaml
  arena-processor-group-one:
    image: "arena-app:dev"
    command: ["/home/apps/start-services.sh", "processor"]
    environment:
      - PROCESSOR_JAVA_OPTS=-Xms256m -Xmx1538m
      - PROCESSOR_GROUP_ID=Group-One
      - PROCESSOR_MAX_TASK_TIME=850000
  arena-processor-group-two:
    image: "arena-app:dev"
    command: ["/home/apps/start-services.sh", "processor"]
    environment:
      - PROCESSOR_JAVA_OPTS=-Xms256m -Xmx1538m
      - PROCESSOR_GROUP_ID=Group-Two
      - PROCESSOR_MAX_TASK_TIME=850000
```

Also you can start multiple processors for a same group.

E.g. start 5 processors for `Group-One`, and start 10 processors for `Group-Two`.

```bash
docker-compose up -d --scale arena-processor-group-one=5
docker-compose up -d --scale arena-processor-group-two=10
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
  # Create a temp container without start it
  docker create --name temp-arena-container arena-app:dev
  # Copy processor-windows.tgz from temp container
  docker cp temp-arena-container:/home/apps/dev/app/dist/processor-windows.tgz .
  # Remove the temp container
  docker rm temp-arena-container
  ```

  Then copy `processor-windows.tgz` to Windows and extract it to`C:` drive.

  The extracted path should be `C:\processor\deploy`.

- Start Windows processor:

  ```
  C:\cygwin64\bin\bash.exe --login -c "cd /cygdrive/c/processor/deploy && ./processor.sh"
  ```

