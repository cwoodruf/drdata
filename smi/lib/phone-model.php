<?php

class PhoneSchedule extends Schedule {
	public function __construct(){
		parent::__construct();
	}
	
	public function tasklist2xml($email,$password) {
		try {
			if ((is_array($tasklist = $this->tasklist($email,$password))) === false) 
				throw new Exception("Invalid Study ID");
				
				return $this->tasks2xml($tasklist);
		} catch (Exception $e) {
			return "ERROR : {$e->getMessage()}";
		}
	}
	
}

