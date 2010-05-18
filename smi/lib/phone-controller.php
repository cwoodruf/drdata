<?php

class PhoneAction extends DoIt { 
	public $actions = array(
		'' => 'doNothing',
		'validateLogin' => 'validateLogin',
		'getTaskList' => 'getTaskList',
		'sendData' => 'sendData',
		'getTask' => 'getTask',
	);
	
	function __construct() {
		parent::__construct('do');
	}
	
	function process(&$a=null) {
		try {
			$this->err = '';
			if (Check::isdatetime($_REQUEST['timestamp']) == false)
				$this->err .= "bad timestamp. ";
			if (preg_match('/^[a-f\d]{32}$/',$_REQUEST['password']) == false)
				$this->err .= "bad password format. ";
			if (preg_match('/[\w\-\.]+@[\w\-\.]+/', $_REQUEST['email']) == false)
				$this->err .= "bad email username. ";
			if ($this->err) throw new Exception($this->err);
			$print = parent::process($a);
			return $print;
		} catch (Exception $e) {
			return "ERROR : $a {$e->getMessage()}";
		}
	}
		
	function doNothing() {
		return "possible actions (do=...):\n".var_export($this->actions,true).
			"\nrequest data:\n".var_export($_REQUEST,true);
	}
		
	function validateLogin() {
		if (!Check::isemail($email = $_REQUEST['email'])) return "ERROR: invalid email!";
		if (!Check::ismd5($password = $_REQUEST['password'])) return "ERROR: bad password hash!";
		$p = new Participant();
		if ($p->enrolled($email,$password)) return "OK";
		return "ERROR: participant $email not found";
	}

	function getTaskList() {	
		$s = new PhoneSchedule;
		return $s->tasklist2xml($_REQUEST['email'],$_REQUEST['password']);
	}
	
	function getTask() {
		$t = new Task;	
		return $t->forms2html($_REQUEST['task_id'],$_REQUEST['study_id']);
	}
	
	function sendData() {
		global $tables;
		$emptyok = false;
		foreach ($tables['drdat_data'] as $field => $fdata) {
			if (($checker = $fdata['checker']) == "") continue;
			$val = $_REQUEST[$field];
			if (!Check::$checker($val)) return "ERROR: invalid $field '$val'!";
			$keys[$field] = $val;
		}

		$keys['sent'] = date('Y-m-d H:i:s');
		$query = array(
			'data' => $_REQUEST['data'],
			'instruction' => $_REQUEST['instruction']
		);
		$keys['query'] = serialize($query);

		$d = new Data;
		return $d->insert_drdat_data($keys);
	}
}

