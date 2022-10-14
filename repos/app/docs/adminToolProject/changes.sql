INSERT INTO monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id)
VALUES (21703, 'Backup tables', 'Backup tables', 1);

INSERT INTO monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id)
VALUES (21705, 'Restore tables', 'Restore tables', 1);

create table backup (
    backup_id DECIMAL(10,0) NOT NULL PRIMARY KEY,
    round_id DECIMAL (10,0) NOT NULL,
    timestamp DATETIME YEAR TO FRACTION NOT NULL,
    comment VARCHAR(254),
    FOREIGN KEY (round_id) REFERENCES round
);

create table backup_tables (
    backup_id DECIMAL(10,0) NOT NULL,
    table_name VARCHAR(18) NOT NULL,
    FOREIGN KEY (backup_id) REFERENCES backup
);

alter table staging_rating add backup_id DECIMAL(10,0) NOT NULL;
alter table staging_challenge add backup_id DECIMAL(10,0) NOT NULL;
alter table staging_problem_state add backup_id DECIMAL(10,0) NOT NULL;
alter table staging_compilation add backup_id DECIMAL(10,0) NOT NULL;
alter table staging_submission add backup_id DECIMAL(10,0) NOT NULL;
alter table staging_room_result add backup_id DECIMAL(10,0) NOT NULL;
alter table staging_system_test_result add backup_id DECIMAL(10,0) NOT NULL;

// After all of the above is done you have to make the newly added backup_id column of the staging tables is part
// of the PRIMARY KEY. I accomplished that by using ServerStudio's GUI. You may wish to do it manually by
// executing commands like:

// EXAMPLE: alter table staging_rating drop constraint staging_rating_pkey;
// EXAMPLE: alter table staging_rating add constraint primary key (coder_id, backup_id);

INSERT INTO monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id)
 VALUES (21650, 'Aggregate', 'Aggregate', 1);

INSERT INTO monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id)
 VALUES (21651, 'Coder', 'Coder', 1);

INSERT INTO monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id)
 VALUES (21652, 'Empty', 'Empty', 1);

INSERT INTO monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id)
 VALUES (21653, 'Rank', 'Rank', 1);

INSERT INTO monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id)
 VALUES (21654, 'Requests', 'Requests', 1);

INSERT INTO monitor_function (monitor_function_id, name, monitor_function_desc, monitor_function_type_id)
 VALUES (21655, 'Round', 'Round', 1);
