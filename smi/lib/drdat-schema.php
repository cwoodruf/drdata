<?php
/*
---------------------------------------------------------------
Author Cal Woodruff cwoodruf@gmail.com
Licensed under the Perl Artistic License version 2.0
http://www.perlfoundation.org/attachment/legal/artistic-2_0.txt
---------------------------------------------------------------
*/
if (__SMI__) die("no direct access.");
# these constants are defined in lib/.localsettings.php
$DRDAT = array(
	'host' => DRDAT_DBHOST,
	'login' => DRDAT_DBLOGIN,
	'pw' => DRDAT_DBPW,
	'db' => DRDAT_DB,
);

# built from mysqldump of drdat database
# use this to build forms, do input checking etc.

$tables = array();

/**
 * the $tables array describes each table in the db in an abstract way
 * and is used by other objects to allow us to abstract how we work with data
 */

# entities:
$tables['drdat_data'] = array(
        'study_id' => array( 'type' => 'int', 'size' => 11, 'checker' => 'digits' ),
        'task_id' => array( 'type' => 'int', 'size' => 11,  'checker' => 'digits'),
        'email' => array( 'type' => 'varchar', 'size' => 64, 'checker' => 'isemail' ),
        'password' => array( 'type' => 'varchar', 'size' => 64, 'checker' => 'ismd5' ),
        'query' => array( 'type' => 'text', ),
        'ts' => array( 'type' => 'datetime', 'checker' => 'isdatetime' ),
        'sent' => array( 'type' => 'datetime', ),
);

$tables['participant'] = array(
  'participant_id' => array( 'type' => 'int', 'size' => 11, 'key' => true ),
  'email' => array( 'type' => 'varchar', 'size' => 128 ),
  'password' => array( 'type' => 'password', 'size' => 64 ),
  'firstname' => array( 'type' => 'varchar', 'size' => 64 ),
  'lastname' => array( 'type' => 'varchar', 'size' => 64 ),
  'phone' => array( 'type' => 'varchar', 'size' => 32 ),
);

$tables['researcher'] = array(
  'researcher_id' => array( 'type' => 'int', 'size' => 1, 'key' => true  ),
  'lastname' => array( 'type' => 'varchar', 'size' => 64 ),
  'firstname' => array( 'type' => 'varchar', 'size' => 64 ),
  'phone' => array( 'type' => 'varchar', 'size' => 32 ),
  'email' => array( 'type' => 'varchar', 'size' => 128 ),
  'password' => array( 'type' => 'password', 'size' => 64 ),
  'position' => array( 'type' => 'varchar', 'size' => 128 ),
  'institution' => array( 'type' => 'varchar', 'size' => 128 ),
);

$tables['study'] = array(
  'study_id' => array( 'type' => 'int', 'size' => 11, 'key' => true  ),
  'study_title' => array( 'type' => 'varchar', 'size' => 42 ),
  'description' => array( 'type' => 'text', 'rows' => 15, 'cols' => 40 ),
  'startdate' => array( 'type' => 'date', 'size' => 20 ),
  'enddate' => array( 'type' => 'date', 'size' => 20 ),
);

$tables['task'] = array(
  'task_id' => array( 'type' => 'int', 'size' => 11, 'key' => true  ),
  'task_title' => array( 'type' => 'varchar', 'size' => 128 ),
  'task_notes' => array( 'type' => 'text', 'rows' => 5, 'cols' => 60 ),
  'formtext' => array( ),
  'forms_locked' => array( 'type' => 'none' ),
  'last_modified' => array( 'type' => 'timestamp', 'size' => 20 )
);

$tables['taskitem'] = array(
  'taskitem_id' => array( 'type' => 'int', 'size' => 11, 'key' => true  ),
  'instruction' => array( 'type' => 'varchar', 'size' => 255 ),
  'format' => array( 'type' => 'text', 'rows' => 5, 'cols' => 60 ),
);

# relations
# associates a researcher with a study
$tables['research'] = array(
  'PRIMARY KEY' => array('researcher_id' => 'researcher', 'study_id' => 'study'),
  'researcher_id' => array( 'type' => 'int', 'size' => 11, 'hide' => true ),
  'study_id' => array( 'type' => 'int', 'size' => 11, 'hide' => true ),
  'visible' => array( 'type' => 'int', 'size' => 11, 'default' => 1 ),
);
  
# associates a task with a study for a given period of time
$tables['schedule'] = array(
  'PRIMARY KEY' => array('task_id' => 'task', 'study_id' => 'study'),
  'task_id' => array( 'type' => 'int', 'size' => 11, 'hide' => true ),
  'study_id' => array( 'type' => 'int', 'size' => 11, 'hide' => true ),
  'startdate' => array( 'type' => 'date', 'size' => 20 ),
  'enddate' => array( 'type' => 'date', 'size' => 20 ),
  'timesofday' => array( 'type' => 'varchar', 'size' => 255, 
	'comment' => '<br><div class="comment">when to alert participant: use 24 hour time. format: HH:MM;...</div>' ),
  'daysofweek' => array( 'type' => 'varchar', 'size' => '64', 'comment' => 
	'<div class="comment">days of the week (Mon,Tue,Wed,Thu,Fri,Sat,Sun) for this task: blank = daily</div>' ),
  'last_modified' => array( 'type' => 'timestamp', 'size' => 20 ),
  'active' => array( 'type' => 'none' ),
  'has_data' => array( 'type' => 'none' ),
);

# groups task items into forms for each task
$schema['form'] = array(
  'PRIMARY KEY' => array('form_id' => 'form', 'task_id' => 'task', 'taskitem_id' => 'taskitem', ),
  'form_id' => array( 'type' => 'int', 'size' => 11, ),
  'task_id' => array( 'type' => 'int', 'size' => 11, ),
  'taskitem_id' => array( 'type' => 'int', 'size' => 11, ),
  'form_ord' => array( 'type' => 'int', 'size' => 11, ),
);

# associates participants to a study
$tables['enrollment'] = array(
  'PRIMARY KEY' => array('participant_id' => 'participant', 'study_id' => 'study'),
  'participant_id' => array( 'type' => 'int', 'size' => 11, 'hide' => true ),
  'study_id' => array( 'type' => 'int', 'size' => 11, 'hide' => true ),
  'enrolled' => array( 'type' => 'datetime', 'size' => 20 ),
  'active' => array( 'type' => 'int', 'size' => 11 ),
);

