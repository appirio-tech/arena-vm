INSERT INTO monitor_function 
(monitor_function_id, name, monitor_function_desc, monitor_function_type_id)
values (21700, 'Request Restart compiler', 'Request Restart Compiler', 2);

INSERT INTO monitor_function 
(monitor_function_id, name, monitor_function_desc, monitor_function_type_id)
values (21701, 'Request Restart Tester', 'Request Rester Tester', 2);

INSERT INTO monitor_function 
(monitor_function_id, name, monitor_function_desc, monitor_function_type_id)
values (21702, 'Request Restart All', 'Request Restart All', 2);

INSERT INTO monitor_function 
(monitor_function_id, name, monitor_function_desc, monitor_function_type_id)
values (21658, 'Request Set Round Terms', 'Request Set Round Terms', 1);

INSERT INTO group_monitor_function_xref
(group_id, monitor_function_id)
values (50, 21700);

INSERT INTO group_monitor_function_xref
(group_id, monitor_function_id)
values (50, 21701);

INSERT INTO group_monitor_function_xref
(group_id, monitor_function_id)
values (50, 21702);

INSERT INTO group_monitor_function_xref
(group_id, monitor_function_id)
values (50, 21658);
