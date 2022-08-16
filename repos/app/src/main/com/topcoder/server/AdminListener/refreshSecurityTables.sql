delete from group_monitor_function_xref;
delete from monitor_user_round_xref;
delete from monitor_function;
delete from monitor_function_type_lu;

insert into monitor_function_type_lu (monitor_function_type_id, monitor_function_type_desc) values(1, "Round-specific command");
insert into monitor_function_type_lu (monitor_function_type_id, monitor_function_type_desc) values(2, "Non round-specific command");

insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(5, "Blob search", "Blob search", 2);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(10, "Object loader", "Object loader", 2);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(15, "Text loader", "Text loader", 2);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(20, "Text search", "Text search", 2);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(200, "Disable round", "Disable round", 1);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(205, "Enable round", "Enable round", 1);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(210, "Refresh round", "Refresh round", 1);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(215, "Restore round", "Restore round", 1);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(400, "Register user", "Register user", 1);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(405, "Unregister user", "Unregister user", 1);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(410, "Refresh problems", "Refresh problems", 1);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(415, "Refresh registration", "Refresh registration", 1);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(420, "Refresh room", "Refresh room", 1);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(425, "Refresh room lists", "Refresh room lists", 1);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(430, "Assign rooms", "Assign rooms", 1);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(435, "Set spectator room", "Set spectator room", 1);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id)
values(600, "Add time", "Add time", 1);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(605, "Start timer", "Start timer", 1);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(610, "Advance contest phase", "Advance contest phase", 1);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(620, "Refresh broadcasts", "Refresh broadcasts", 1);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(625, "Create system test", "Create system test", 1);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(630, "Consolidate test cases", "Consolidate test cases", 1);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(635, "Clear test cases", "Clear test cases", 1);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(640, "Run system test", "Run system test", 1);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(800, "End contest", "End contest", 1);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(805, "Allocate prizes", "Allocate prizes", 1);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(810, "Run ratings", "Run ratings", 1);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(815, "Insert practice room", "Insert practice room", 1);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(1000, "Disconnect client", "Disconnect client", 2);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(1005, "Load next active contest", "Load next active contest", 2);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(1010, "Shutdown contest listeners", "Shutdown contest listeners", 2);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(1015, "Initiate contest server garbage collection", "Initiate contest server garbage collection", 2);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(1020, "Start replay listener", "Start replay listener", 2);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(1025, "Start replay receiver", "Start replay receiver", 2);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(1200, "Request cached coder", "Request cached coder", 1);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(1205, "Request cached coder problem", "Request cached coder problem", 1);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(1210, "Request cached problem", "Request cached problem", 1);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(1215, "Request cached registration", "Request cached registration", 1);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(1220, "Request cached room", "Request cached room", 1);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(1225, "Request cached round", "Request cached round", 1);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(1230, "Request cached user", "Request cached user", 1);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(1400, "Ban IP", "Ban IP", 2);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(1405, "Grant admin authority", "", 2);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(1410, "Revoke admin authority", "", 2);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(1415, "Set user status", "Set user status", 2);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(1420, "Contest management", "Contest management", 2);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(1425, "Moderated chat", "Moderated chat", 2);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(1430, "Logging", "Logging", 2);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(1600, "Send global broadcast", "Send global broadcast", 2);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(1605, "Send problem broadcast", "Send problem broadcast", 1);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(1610, "Send round broadcast", "Send round broadcast", 1);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(2000, "Chat view", "Chat view", 2);
insert into monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id) 
values(21657, "Request New ID", "Request New ID", 1);

