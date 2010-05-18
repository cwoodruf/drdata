<?php
/*
---------------------------------------------------------------
Author Cal Woodruff cwoodruf@gmail.com
Licensed under the Perl Artistic License version 2.0
http://www.perlfoundation.org/attachment/legal/artistic-2_0.txt
---------------------------------------------------------------
*/
# use this file for code relating to user input
if (__SMI__) die("no direct access.");

class Doit {
	public $action;
	
	public function __construct($a='action') {
		if (Check::isvar($a) ===FALSE) die("don't understand action!");
		$this->action = $a;
	}
	
	public function process(&$action=null) {
		# find the method from the map of action -> method
		if (Check::isvar($action,($empty=false)) == false) {
			$action = $this->get();	
		}
		$func = $this->actions[$action];
		# if it doesn't exist use the default
		if (empty($func) or !method_exists($this,$func)) 
			$func = $this->actions[''];
		# run the function and return the result (a template name for the page to view)
		return $this->$func();
	}
	
	public static function action($a='action') {
		return $_REQUEST[$a];
	}

	public function get() {
		return self::action($this->action);
	}

	public function valid() {
		return $this->actions[$this->get()] ? true : false;
	}

	public function unblocked() {
		return $this->unblocked[$this->get()] ? true : false;
	}

	public function err(Exception $e=null) {
		if ($e) $this->error = $e->getMessage();
		return $this->error;
	}

}

