create table component_type_lu (
    component_type_id DECIMAL(3,0),
    component_type_desc VARCHAR(255)
)
in dev_tbldbs
extent size 16 next size 16
lock mode row;
alter table component_type_lu add constraint primary key 
	(component_type_id)
	constraint conmponent_type_lu_pk;

INSERT INTO component_type_lu VALUES (1,'Main Component');

create table component (
    component_id DECIMAL(10,0) not null,
    problem_id DECIMAL(10,0) not null,
    result_type_id DECIMAL(5) not null,
    method_name VARCHAR(32) not null,
    class_name VARCHAR(32) not null,
    default_solution TEXT,
    component_type_id DECIMAL(3,0),
    component_text TEXT,
    status_id DECIMAL(3,0),
    modify_date DATETIME YEAR TO FRACTION
)
in dev_tbldbs
extent size 5120 next size 2048
lock mode row;
alter table component add constraint primary key 
	(component_id)
	constraint component_pk;
alter table component add constraint foreign key 
	(component_type_id)
	references component_type_lu
	(component_type_id) 
	constraint component_componenttypelu_fk;

INSERT INTO component 
	SELECT p.problem_id, p.problem_id, p.result_type_id, 
		p.method_name,p.class_name,p.default_solution,1,
		p.problem_text, 1, p.modify_date FROM problem p;



create table problem_type_lu (
    problem_type_id DECIMAL(3,0) not null,
    problem_type_desc VARCHAR(255) not null
)
in dev_tbldbs
extent size 16 next size 16
lock mode row;
alter table problem_type_lu add constraint primary key 
	(problem_type_id)
	constraint problem_type_lu_pk;

INSERT INTO problem_type_lu VALUES (1,'Single');
INSERT INTO problem_type_lu VALUES (2,'Team');

ALTER TABLE problem ADD problem_type_id DECIMAL(3,0) BEFORE proposed_difficulty_id;

alter table problem add constraint foreign key 
	(problem_type_id)
	references problem_type_lu
	(problem_type_id) 
	constraint problem_problemtypelu_fk;

UPDATE problem SET problem_type_id = 1;


create table parameter (
    parameter_id DECIMAL(10,0) not null,
    component_id DECIMAL(10,0) not null,
    data_type_id DECIMAL(5,0) not null,
    name VARCHAR(50) not null,
    sort_order DECIMAL(3,0) not null
)
in dev_tbldbs
extent size 512 next size 512
lock mode row;
alter table parameter add constraint foreign key 
	(data_type_id)
	references data_type
	(data_type_id) 
	constraint parameter_datatype_fk;
alter table parameter add constraint primary key 
	(parameter_id)
	constraint parameter_pk;


create table component_solution_xref (
    component_id DECIMAL(10,0) not null,
    solution_id DECIMAL(10,0) not null,
    primary_solution DECIMAL(1,0)
)
in dev_tbldbs
extent size 32 next size 32
lock mode row;
alter table component_solution_xref add constraint primary key 
	(component_id, solution_id)
	constraint component_solution_xref_pk;
alter table component_solution_xref add constraint foreign key 
	(solution_id)
	references solution
	(solution_id) 
	constraint componentsolution_solution_fk;

INSERT INTO component_solution_xref (component_id,solution_id) SELECT problem_id,solution_id FROM problem_solution;
UPDATE component_solution_xref SET primary_solution=0 WHERE solution_id IN ( SELECT solution_id FROM problem_solution WHERE primary_solution = 'N' );
UPDATE component_solution_xref SET primary_solution=1 WHERE solution_id IN ( SELECT solution_id FROM problem_solution WHERE primary_solution = 'Y' );

create table component_status_lu (
    component_status_id DECIMAL(3,0),
    status_desc VARCHAR(100)
)
in dev_tbldbs
extent size 32 next size 32
lock mode row;
alter table component_status_lu add constraint primary key 
	(component_status_id)
	constraint component_status_lu_pkey;

INSERT INTO component_status_lu VALUES (100,'Active');
INSERT INTO component_status_lu VALUES (110,'Unopened');
INSERT INTO component_status_lu VALUES (120,'Opened');
INSERT INTO component_status_lu VALUES (121,'Compiled');
INSERT INTO component_status_lu VALUES (130,'Submitted');
INSERT INTO component_status_lu VALUES (131,'Challenge Failed');
INSERT INTO component_status_lu VALUES (140,'Challenge Succeeded');
INSERT INTO component_status_lu VALUES (150,'Passed System Test');
INSERT INTO component_status_lu VALUES (160,'Failed System Test');


RENAME TABLE problem_state TO component_state;
RENAME COLUMN component_state.problem_state_id TO component_state_id;
RENAME COLUMN component_state.problem_id TO component_id;

alter table component_state add constraint foreign key 
	(status_id)
	references component_status_lu
	(component_status_id) 
	constraint componentstate_componentstatuslu_fk;

alter table component_state add constraint foreign key 
	(component_id)
	references component
	(component_id) 
	constraint componentstate_component_fk;

alter table component_state DROP constraint problem_problemstate_fk; 
alter table component_state DROP constraint problemstate_problemstatus_fk;


create table data_type_mapping (
    language_id DECIMAL(3,0) not null,
    display_value VARCHAR(50),
    data_type_id DECIMAL(5,0) not null
)
in dev_tbldbs
extent size 16 next size 16
lock mode row;
alter table data_type_mapping add constraint primary key 
	(language_id, data_type_id)
	constraint data_type_mapping_pk;
alter table data_type_mapping add constraint foreign key 
	(language_id)
	references language
	(language_id) 
	constraint datatypemapping_language_fk;
alter table data_type_mapping add constraint foreign key 
	(data_type_id)
	references data_type
	(data_type_id) 
	constraint datatypemapping_datatype_fk;

INSERT INTO data_type_mapping VALUES(1,'int',1);
INSERT INTO data_type_mapping VALUES(1,'float',4);
INSERT INTO data_type_mapping VALUES(1,'char',6);
INSERT INTO data_type_mapping VALUES(1,'byte',7);
INSERT INTO data_type_mapping VALUES(1,'short',13);
INSERT INTO data_type_mapping VALUES(1,'long',14);
INSERT INTO data_type_mapping VALUES(1,'double',15);
INSERT INTO data_type_mapping VALUES(1,'String',18);
INSERT INTO data_type_mapping VALUES(1,'boolean',19);
INSERT INTO data_type_mapping VALUES(1,'int[]',20);
INSERT INTO data_type_mapping VALUES(1,'double[]',21);
INSERT INTO data_type_mapping VALUES(1,'String[]',22);
INSERT INTO data_type_mapping VALUES(3,'int',1);
INSERT INTO data_type_mapping VALUES(3,'float',4);
INSERT INTO data_type_mapping VALUES(3,'char',6);
INSERT INTO data_type_mapping VALUES(3,'byte',7);
INSERT INTO data_type_mapping VALUES(3,'short',13);
INSERT INTO data_type_mapping VALUES(3,'long long',14);
INSERT INTO data_type_mapping VALUES(3,'double',15);
INSERT INTO data_type_mapping VALUES(3,'string',18);
INSERT INTO data_type_mapping VALUES(3,'bool',19);
INSERT INTO data_type_mapping VALUES(3,'vector <int>',20);
INSERT INTO data_type_mapping VALUES(3,'vector <double>',21);
INSERT INTO data_type_mapping VALUES(3,'vector <string>',22);
INSERT INTO data_type_mapping VALUES(4,'int',1);
INSERT INTO data_type_mapping VALUES(4,'float',4);
INSERT INTO data_type_mapping VALUES(4,'char',6);
INSERT INTO data_type_mapping VALUES(4,'byte',7);
INSERT INTO data_type_mapping VALUES(4,'short',13);
INSERT INTO data_type_mapping VALUES(4,'long',14);
INSERT INTO data_type_mapping VALUES(4,'double',15);
INSERT INTO data_type_mapping VALUES(4,'string',18);
INSERT INTO data_type_mapping VALUES(4,'bool',19);
INSERT INTO data_type_mapping VALUES(4,'int[]',20);
INSERT INTO data_type_mapping VALUES(4,'double[]',21);
INSERT INTO data_type_mapping VALUES(4,'String[]',22);


RENAME TABLE round_problem TO round_component;
RENAME COLUMN round_component.problem_id TO component_id;


alter table round_component add constraint foreign key 
	(component_id)
	references component
	(component_id) 
	constraint roundcomponent_component_fk;

alter table round_component drop constraint roundproblem_problem_fk;



INSERT INTO sequence_object SELECT 30,MAX(c.component_id) + 1,'COMPONENT_SEQ' FROM component c;
INSERT INTO sequence_object VALUES ( 31,0,'PARAMETER_SEQ' );


RENAME column challenge.problem_id TO component_id;

ALTER TABLE challenge ADD constraint FOREIGN KEY 
	(component_id) REFERENCES component (component_id)
	CONSTRAINT challenge_component_fk;

ALTER TABLE challenge DROP constraint challenge_problem_fk;


ALTER TABLE solution ADD language_id DECIMAL(3,0);
ALTER TABLE solution ADD package VARCHAR(255,0);

ALTER TABLE solution ADD constraint FOREIGN KEY (language_id) REFERENCES language (language_id)
	CONSTRAINT solution_language_fk;

UPDATE solution SET language_id = 1;
UPDATE solution SET package = 'com.topcoder.tester.solutions.s' || solution_id;

create table solution_class_file (
    solution_id DECIMAL(10,0),
    sort_order DECIMAL(3,0),
    path VARCHAR(255),
    class_file BYTE
)
in dev_tbldbs
extent size 5120 next size 5120
lock mode row;
alter table solution_class_file add constraint primary key 
	(solution_id, sort_order)
	constraint solutionclassfile_pkey;
alter table solution_class_file add constraint foreign key 
	(solution_id)
	references solution
	(solution_id) 
	constraint solutionclassfile_solution_fk;

RENAME TABLE problem_user TO component_user_xref;
RENAME COLUMN component_user_xref.problem_id TO component_id;

alter table component_user_xref add constraint foreign key 
	(component_id)
	references component
	(component_id) 
	constraint componentuserxref_component_fk;

alter table component_user_xref DROP constraint problemuser_problem_fk;


RENAME column compilation.problem_state_id TO component_state_id;

create table compilation_class_file (
    component_state_id DECIMAL(10,0),
    sort_order DECIMAL(3,0),
    path VARCHAR(255),
    class_file BYTE
)
in dev_tbldbs
extent size 51200 next size 51200
lock mode row;
alter table compilation_class_file add constraint primary key 
	(component_state_id, sort_order)
	constraint compilationclassfile_pkey;
alter table compilation_class_file add constraint foreign key 
	(component_state_id)
	references component_state
	(component_state_id) 
	constraint compilationclassfile_component_state_fk;


RENAME COLUMN submission.problem_state_id TO component_state_id;

create table submission_class_file (
    component_state_id DECIMAL(10,0),
    submission_number DECIMAL(5,0),
    sort_order DECIMAL(3,0),
    path VARCHAR(255),
    class_file BYTE
)
in dev_tbldbs
extent size 51200 next size 51200
lock mode row;
alter table submission_class_file add constraint primary key 
	(component_state_id, submission_number, sort_order)
	constraint submissionclassfile_pk;
alter table submission_class_file add constraint foreign key 
	(component_state_id, submission_number)
	references submission
	(component_state_id, submission_number) 
	constraint submissionclassfile_submission_fk;


RENAME COLUMN system_test_case.problem_id TO component_id;

ALTER TABLE system_test_case ADD constraint FOREIGN KEY 
	(component_id) REFERENCES component (component_id)
	CONSTRAINT systemtestcase_component_fk;

ALTER TABLE system_test_case ADD status DECIMAL(3,0);
ALTER TABLE system_test_case ADD example_flag DECIMAL(1,0);


RENAME COLUMN system_test_result.problem_id TO component_id;

ALTER TABLE system_test_result ADD constraint FOREIGN KEY 
	(component_id) REFERENCES component (component_id)
	CONSTRAINT systemtestresult_component_fk;

ALTER TABLE system_test_result DROP constraint system_testcases_problem_fk;

RENAME TABLE problem_status TO problem_status_lu;
RENAME COLUMN problem.status TO status_id;
RENAME COLUMN problem.class_name TO name;


create table web_service (
    web_service_id DECIMAL(10,0),
    web_service_name VARCHAR(100),
    status_id DECIMAL(3,0)
)
in dev_tbldbs
extent size 1024 next size 1024
lock mode row;
alter table web_service add constraint primary key 
	(web_service_id)
	constraint web_service_pkey;


create table web_service_file_type (
    web_service_file_type_id DECIMAL(10,0),
    description VARCHAR(255)
)
in dev_tbldbs
extent size 32 next size 32
lock mode row;
alter table web_service_file_type add constraint primary key 
	(web_service_file_type_id)
	constraint webservicefiletype_pk;



create table web_service_source_file (
    web_service_source_file_id DECIMAL(10,0),
    web_service_file_type_id DECIMAL(10,0),
    web_service_id DECIMAL(10,0),
    language_id DECIMAL(3,0),
    path VARCHAR(255),
    source TEXT
)
in dev_tbldbs
extent size 1024 next size 1024
lock mode row;
alter table web_service_source_file add constraint primary key 
	(web_service_source_file_id)
	constraint webservicesourcefile_pk;
alter table web_service_source_file add constraint foreign key 
	(web_service_id)
	references web_service
	(web_service_id) 
	constraint webservicesourcefile_webservice_fk;
alter table web_service_source_file add constraint foreign key 
	(language_id)
	references language
	(language_id) 
	constraint webservicesourcefile_language_fk;
alter table web_service_source_file add constraint foreign key 
	(web_service_file_type_id)
	references web_service_file_type
	(web_service_file_type_id) 
	constraint webservicesourcefile_webservicefiletype_fk;


create table web_service_compilation (
    web_service_source_file_id DECIMAL(10,0),
    sort_order DECIMAL(3,0),
    web_service_file_type_id DECIMAL(10,0),
    path VARCHAR(255),
    class_file BYTE
)
in dev_tbldbs
extent size 1024 next size 1024
lock mode row;
alter table web_service_compilation add constraint primary key 
	(web_service_source_file_id, sort_order)
	constraint webservicecompilation_pk;
alter table web_service_compilation add constraint foreign key 
	(web_service_source_file_id)
	references web_service_source_file
	(web_service_source_file_id) 
	constraint webservicecompilation_webservicesourcefile_fk;
alter table web_service_compilation add constraint foreign key 
	(web_service_file_type_id)
	references web_service_file_type
	(web_service_file_type_id) 
	constraint webservicecompilation_webservicefiletype_fk;


create table component_web_service_xref (
    component_id DECIMAL(10,0),
    web_service_id DECIMAL(10,0)
)
in dev_tbldbs
extent size 32 next size 32
lock mode row;
alter table component_web_service_xref add constraint primary key 
	(component_id, web_service_id)
	constraint component_web_service_xref_pk;
alter table component_web_service_xref add constraint foreign key 
	(component_id)
	references component
	(component_id) 
	constraint componentwebservice_component_fk;
alter table component_web_service_xref add constraint foreign key 
	(web_service_id)
	references web_service
	(web_service_id) 
	constraint componentwebservice_webservice_fk;

UPDATE monitor_function SET monitor_function_desc='Load Round', name='Load Round', monitor_function_type_id = 1 WHERE monitor_function_id=1005;
INSERT INTO monitor_function VALUES (1006,'Unload Round','Unload Round',1);

DELETE FROM group_monitor_function_xref WHERE monitor_function_id=435;
DELETE FROM monitor_function WHERE monitor_function_id=435;
INSERT INTO monitor_function VALUES (816,'Announce Advancing Coders','Announce Advancing Coders',1);
INSERT INTO monitor_function VALUES (1030,'Start Spec App Rotation','Start Spec App Rotation',2);
INSERT INTO monitor_function VALUES (1035,'Stop Spec App Rotation','Stop Spec App Rotation',2);
INSERT INTO monitor_function VALUES (1040,'Set Spec App Room','Set Spec App Room',2);
INSERT INTO group_monitor_function_xref VALUES(50,816);
INSERT INTO group_monitor_function_xref VALUES(50,1030);
INSERT INTO group_monitor_function_xref VALUES(50,1035);
INSERT INTO group_monitor_function_xref VALUES(50,1040);

RENAME COLUMN broadcast.problem_id TO component_id;

ALTER TABLE broadcast DROP constraint broadcast_problem_fk;
alter table broadcast add constraint foreign key 
	(component_id)
	references component
	(component_id) 
	constraint broadcast_component_fk;