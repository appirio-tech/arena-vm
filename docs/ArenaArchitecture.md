## Arena architecture

![arena](./screenshots/arena.png)



### JBoss

JBoss hosts EJBs which access Ldap/Informix. It uses 1299 port to handle EJB invocations from Listeners.

### Listeners

There are 4 listeners:

- Main Listener handles arena client requests at 5001 port. **The 5001 port is exposed to external.**
- Admin Listener handles admin client requests at 5003 port.**The 5003 port is exposed to external.**
  - It will pass some of these admin requests to 5002 port of Main Listener . For example, assuming admin wants to ban the IP of an arena client, this admin request is first sent to Admin Listener on 5003 port, then passed to Main Listener on 5002 port, then Main Listener will configure itself to ban this IP from arena client.
- WebSocket Listener handles arena web client requests at 5016 port.**The 5016 port is exposed to external.**
  - It will pass all theses WebSocket requests to 5555 port of Main Listener, since it's a wrapper using WebSocket protocol.
  - Besides using WebSocket requests, the arena web client also sends some request to tc-api, like to get SRM schedule.
- MPSQAS Listener handles MPSQAS client requests at 5037 port.**The 5037 port is exposed to external.**

### Farm Controller

- JBoss & Main Listener connect to Farm Controller at port 25000. Farm Processors connect to Farm Controller at port 25001.
- JBoss & Main Listener send compile/test invocations to Farm Controller. Farm Controller stores these invocations in mysql table `FARM_INVOCATION`, then assign them to appropriate Farm Processor to process. Farm Controller will periodically scan `FARM_INVOCATION` table for maintainance, like reschedule unsolved invocations, or purge outdated invocations.
- Farm Processors process the compile/test, then report the result to Farm Controller. Farm Controller delete processed invocations from `FARM_INVOCATION` table and send the result back to JBoss/Main Listener.

### Farm Processors

- Farm Processors are organized by groups, within each group there can be multiple processors. Different groups can handle different kind of tasks, e.g. you can define a group to handle SRM tests only, and another group to handle MM tests only. Mysql `FARM_PROC_PROPERTIES_MAP` table has `PROPERTY_NAME` and `PROPERTY_VALUE` columns to define what tasks a group can handle:
  - Property `os.type` can be either `linux` or `windows`. The OS type usually relates to language: linux processor can handle Java/C++/Python languages, windows processor can handle C#/VB.NET languages. 
  - Property `deployed.modules` defines modules to handle: `compiler`, `srm-test` and `long-test`. A group can handle multiple modules.
  - For `compiler` module,  `deploy.module.compiler.languages` defines the languages to handle, refer to informix table `language` for language values; `deploy.module.compiler.roundTypes` defines the round types to handle, refer to informix table `round_type_lu` for round type values; `deploy.module.compiler.action` defines the actions to handle, refer to [ServicesConstants.java](https://github.com/appirio-tech/arena-tc-shared/blob/master/src/main/com/topcoder/shared/common/ServicesConstants.java#L155-L167) for action values.
  - For `srm-test` module, similarly there are `deploy.module.srm-test.languages`, `deploy.module.srm-test.roundTypes` and `deploy.module.srm-test.action` properties.
  - For `long-test` module, similarly there are  `deploy.module.long-test.languages` and `deploy.module.long-test.roundTypes` properties.

- When start a processor, you must specify the group it belongs to. The group name is defined in mysql `FARM_PROCESSOR_PROPERTIES.NAME` column. The built docker image supports an environment variable `PROCESSOR_GROUP_ID` to specify the group name.
- The compile/test time limit are defnined in informix `component.component_text` column. Each contest can have its own compile/test time limit. And a processor has a hard max time limit for any task it's processing regardless of contests. The built docker image uses an environment variable `PROCESSOR_MAX_TASK_TIME` to specify the max task time.

### Deploy Suggestion

- JBoss and those 4 Listeners could be deployed together within same host/container to avoid network latency.
- Farm Controller periodically scan mysql tables for maintainance,  it can be deployed along with mysql in same host.
- Multiple Farm Processors can be deployed for each group to scale.