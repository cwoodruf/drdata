<?php
/*
---------------------------------------------------------------
Author Cal Woodruff cwoodruf@gmail.com
Licensed under the Perl Artistic License version 2.0
http://www.perlfoundation.org/attachment/legal/artistic-2_0.txt
---------------------------------------------------------------
*/
# use this file for code relating to data access and business logic
if (__SMI__) die("no direct access.");
# dbabstracter will need the schema file 
require('lib/drdat-schema.php');

# note that this is part of another project in google code 
# http://code.google.com/p/dbabstracter4php/
require('.db/abstract-mysql.php');
require('.db/abstract-common.php');

function ascii_key($count) {
	if (ord('C') + $count > ord('Z')) {
		$key = 'A'.chr($count+2);
	} else $key = chr(ord('C')+$count);
	return $key;
}

/*
 * entity classes
 */
class Data extends Entity {
	public function __construct() {
		global $DRDAT, $tables;
		parent::__construct($DRDAT, $tables['drdat_data'], 'drdat_data');
	}

	function latest_upload($study_id,$task_id=null,$email=null) {
		if (!Check::digits($study_id)) 
			throw new Exception("Data->toCSV: bad study_id $study_id");
		$clauses[] = "study_id=$study_id";

		if (Check::digits($task_id)) {
			$clauses[] = "task_id=$task_id";
		}

		if (Check::isemail($email)) { 
			$clauses[] = sprintf(
				"email='%s'",
				$this->quote($email),$this->quote($passwordMD5)
			);
		}
		try {
			$where = implode(" and ",$clauses);
			$this->run("select max(ts) as latestupload from drdat_data where $where");
			$row = $this->getnext();
			$this->free();
			return $row['latestupload'];

		} catch (Exception $e) {
			$this->err($e);
			return false;
		}
	}

	function insert_drdat_data($keys) {
		$task = new Task;
		$sched = new Schedule;
		try {
			$this->ins($keys);
			$task->upd($keys['task_id'], array('forms_locked' => 1));
			return "OK ".$keys['sent'];

		} catch (Exception $e) {
			$this->err($e);
			return false;
		}
	}

	public function task2CSV($study_id, $task_id=null, $email=null, $passwordMD5=null) {
		Check::$emptyok = false;

		if (!Check::digits($study_id)) 
			throw new Exception("Data->toCSV: bad study_id $study_id");

		$clauses[] = "study_id=$study_id";
		if (Check::digits($task_id)) {
			$clauses[] = "task_id=$task_id";
		}

		if (Check::isemail($email) and Check::ismd5($passwordMD5)) { 
			$clauses[] = sprintf(
				"email='%s' and password='%s'",
				$this->quote($email),$this->quote($passwordMD5)
			);
		}

		Check::$emptyok = true;

		$badchars = '#([\n\r,]+)#';
		$replace = create_function(
			'$m', // input = matches from regexp above
			'return $m[1] == "," ? "&comma;": "<br>";' // fix by formatting as html
		);
		try {
			$where = implode(" and ",$clauses);
			$this->run("select email,ts,query from drdat_data where $where order by ts desc");
			$rows = array();
			$instructions = array();
			while ($r = $this->getnext()) {
				$row = array();
				$d = unserialize($r['query']);
				$entered = $d['data'];
				$inst = $d['instruction'];
				if (is_array($inst)) {
					foreach ($inst as $count => $i) {
						$i = preg_replace_callback($badchars,$replace,trim($i));
						$key = ascii_key($count);
						$instructions[$key] = empty($i) ? '(no instruction)': $i;
					}
				}
				$row['A'] = $r['ts'];
				$row['B'] = $r['email'];
				for ($count=0; $count<count($instructions); $count++) {
					$entry = $entered[$count];
					if (is_array($entry)) {
						$entry = implode("|",$entry);
					}
					$entry = preg_replace_callback($badchars,$replace,$entry);
					$key = ascii_key($count);
					$row[$key] = $entry;
				}
				$rows[] = $row;
			}
			$this->free;
			$csv = "Legend:\nColumn,Data\nA,Date\nB,Participant\n";
			foreach ($instructions as $letter => $instruction) {
				$csv .= "$letter,$instruction\n";
			}
			$csv .= "\nData\n";
			foreach ($rows as $row) {
				foreach ($row as $letter => $datum) {
					$csv .= "$datum,";
				}
				$csv .= "\n";
			}
	
			return $csv;

		} catch (Exception $e) {
			$this->err($e);
			return false;
		}
	}
}

class Researcher extends Entity {
	public function __construct() {
		global $DRDAT, $tables;
		parent::__construct($DRDAT, $tables['researcher'], 'researcher');
	}

	public function validate($email,$password) {
		try {
			if (!Check::isemail($email)) throw new Exception("bad email!");
			$md5pw = md5($password);
			$this->run(
				"select * from researcher where email='%s' and password='%s'",
				$email, $md5pw
			);
			$row = $this->getnext();
			$this->free();
			return $row;

		} catch (Exception $e) {
			$this->err($e);
			return false;
		}
	}

	public function validstudy($study_id=null) {
		Check::$emptyok = false;
		$rid = $_SESSION['user']['researcher_id'];

		if ($study_id === null) $study_id = $_REQUEST['study_id'];
		if (empty($study_id)) return true;

		if (!Check::digits($rid)) return false;
		if (!Check::digits($study_id)) return false;
		Check::$emptyok = true;

		try {
			$this->run(
				"select count(*) as valid from research natural join researcher ".
				"where researcher.researcher_id=%u and study_id=%u",
				$rid, $study_id
			);
			$valid = $this->getnext();
			$this->free();
			return ($valid['valid'] > 0);

		} catch (Exception $e) {
			$this->err($e);
			return false;
		}
	}
}

class Study extends Entity {
	public function __construct() {
		global $DRDAT, $tables;
		parent::__construct($DRDAT,$tables['study'], 'study');
	}
	
	public function study($rid,$study_id) {
		try {
			if (!Check::digits($rid)) throw new Exception("bad researcher id!");
			if (!Check::digits($study_id)) throw new Exception("bad study id!");
			$this->run(
				"select study.*,research.visible ".
				"from study join research using (study_id) ".
				"where research.researcher_id=%u and study.study_id=%u ",
				$rid, $study_id
			);
			$row = $this->getnext();
			$this->free();
			return $row;

		} catch (Exception $e) {
			$this->err($e);
			return false;
		}
	}
	public function studies($rid,$all=false) {
		try {
			if (!Check::digits($rid,($empty=false))) throw new Exception("bad researcher id!");
			if (!$all) $showvisible = " and research.visible = 1 ";
		
			$this->run(
				"select study.*,research.visible ".
				"from study join research using (study_id) ".
				"where research.researcher_id=%u ".
				$showvisible.
				"order by startdate desc",
				$rid,$visible
			);
			return $this->resultarray();

		} catch (Exception $e) {
			$this->err($e);
			return false;
		}
	}
}

class Participant extends Entity {
	public function __construct() {
		global $DRDAT, $tables;
		parent::__construct($DRDAT,$tables['participant'], 'participant');
	}

	# check if this participant is enrolled in any study
	public function enrolled($email,$password) {
		try {
			$this->run(
				"select enrollment.active ". 
				"from participant join enrollment using (participant_id) ".
				"where email='%s' and password='%s' and active = 1 ",
				$email, $password
			);
			$valid = $this->num() ? true : false;
			return $valid;
			
		} catch (Exception $e) {
			$this->err($e);
			return false;
		}
	}

	public function studyparts($rid, $study_id, $active=1) {
		try {
			if (!Check::digits($rid,($empty=false))) throw new Exception("bad researcher id!");
			if (!Check::digits($study_id,($empty=false))) throw new Exception("bad study id!");
			if (is_integer($active)) $active = " and active = $active ";

			$this->run(
				"select p.*, e.active, max(dd.ts) as latest_update ". 
				"from participant p join enrollment e using (participant_id) ".
				"join research r using (study_id) ".
				"left outer join drdat_data dd on ".
				" (dd.study_id=r.study_id and p.email=dd.email) ".
				"where e.study_id=%u and r.researcher_id=%u $active ".
				"group by p.participant_id",
				$study_id, $rid
			);
			return $this->resultarray();
			
		} catch (Exception $e) {
			$this->err($e);
			return false;
		}
	}
}

class Task extends Entity {
	public $forms;
	public $form;
	public $task;

	public function __construct() {
		global $DRDAT, $tables;
		parent::__construct($DRDAT,$tables['task'], 'task');
	}

	public function tasks($study_id,$rid,$all=false) {
		try {
			if (!Check::digits($study_id,($empty=false))) 
				throw new Exception("bad study id!");
			if (!Check::digits($rid)) 
				throw new Exception("bad researcher id!");
			if ($rid) $rquery = "and research.researcher_id=%u";
			if (!$all) $showall = "and schedule.active = 1 ";
			$this->run(
				"select task.*,schedule.*,max(ts) as latest_update ".
				"from task join schedule using (task_id) ".
				"join research using (study_id) ".
				"join study using (study_id) ".
				"left outer join drdat_data on (drdat_data.task_id=task.task_id) ".
				"where schedule.study_id=%u $rquery $showall ".
				"group by task.task_id ".
				"order by task.task_id",
				$study_id, $rid
			);
			return $this->resultarray();

		} catch (Exception $e) {
			$this->err($e);
			return false;
		}
	}

	/**
	 * turn the formtext field into a data structure that
	 * can be used to make xml or test the form output
	 */
	public function parseforms($task_id,$style='xml') {
		$this->task = $this->getone($task_id);
		return $this->parseformstring($style);
	}
	
	public function parseformstring($style='xml') {
		$raw = trim($this->task['formtext']);
		$lines = explode("\n", preg_replace('/\r/','',$raw));
		$q = $w = $i = false;
		$instructions = array();
		$instruction = null;
		$this->form = 0;
		$items = array();
		$widget = '';
		$inum = -1;
		$inputs = "checkbox|dropdown|text";
		$widgetpat = "#$inputs#"; 
		$widgetcap = "#^($inputs|none)#";
		foreach ($lines as $line) {
			if (preg_match('/^\s*#/', $line)) 
				continue;

			if (preg_match('#^--#', $line)) {
				$this->addinstruction(
					$inum,$instruction,$widget,$items,$style
				);
				if (count($this->forms[$this->form])) $this->form++;
				continue;
			}

			if (preg_match('#^(\w):(.*)#',$line, $m)) {
				$code = strtolower($m[1]);
				$details = trim($m[2]);
				switch($code) {
					case 'i': 
						$this->addinstruction(
							$inum,$instruction,$widget,$items,$style
						);
						$instruction = $details;
					break;
					case 'w': 
						if (preg_match($widgetcap,$details)) {
							$widget = $details;
							if ($widget == 'text') $items[0] = null;
						}
					break;
					case 'o': 
						switch ($style) {
						case 'html':
							switch($widget) {
							case 'checkbox':
							case 'dropdown':
								$items[] = $details;
							break;
							}
						break;
						default: $items[] = urlencode($details);
						}
					break;
				}
				continue;
			} 
			if ($widget == '') 
				$instruction .= "\n".$line;
		}
		$this->addinstruction($inum,$instruction,$widget,$items,$style);
		return $this->forms;
	}

	private function addinstruction(&$inum, &$instruction, &$widget, &$items, $style='xml') {
		if ($instruction !== null) {
			if ($widget == 'text' or 
				(preg_match('#dropdown|checkbox#',$widget) and count($items) > 0)) {
				$inum++;
			}

			$format = '';
			if ($style == 'html') 
				$htmlstart = "<input type=\"hidden\" name=\"instruction[$inum]\" ".
					"value=\"".htmlentities($instruction)."\">\n";
			if (preg_match('#^(text).*?(\d+\s*,\s*\d+)#',$widget,$m)) {
				$widget = $m[1];
				list($textcols,$textrows) = explode(",", $m[2]);
			}
			switch ($widget) {
				case 'dropdown':
					if ($style == 'html') {
						$htmlstart .= "<select name=\"data[$inum]\"><option></option>\n";
						$htmlend = "</select>"; 
						if (count($items)) {
							$format = $htmlstart;
							foreach ($items as $item) {
								$format .= "<option>$item</option>\n";
							}
							$format .= $htmlend;
						}
						break;
					}
				case 'checkbox':
					if (count($items)) {
						switch($style) {
						case 'html':
							$format = $htmlstart;
							foreach ($items as $cnum => $item) {
								$format .= "<input type=checkbox ".
									"name=\"data[$inum][$cnum]\" ".
									"value=\"$item\"> $item\n";
							}
							$format .= $htmlend;
						break;
						default: $format = $widget.':'.implode('&',$items);
						}
					}
				break;
				case 'text':
					if ($style == 'html') {
						if ($textcols > 0 and $textrows > 0) {
							$format = <<<HTML
$htmlstart
<textarea name="data[$inum]" 
          rows="$textrows" 
	  cols="$textcols"></textarea>
HTML;
						} else {
							$format = "$htmlstart<input name=\"data[$inum]\">";
						}
						break;
					}
				case 'none':
				default: 
					if ($style == 'html') {
						# ignore instructions where there is no data input?
						$format = ""; # $htmlstart;
					} else {
						$format = 'none';
					}
			}	
			$this->forms[$this->form][] = 
				array(
					'instruction' => $instruction,
					'format' => $format,
				);
		}
		$instruction = null;
		$widget = '';
		$items = array();
	}

	/**
	 * make the $forms member and output it as xml
	 * this is what gets sent when a phone requests details for a task
	 */
	public function forms2xml($task_id, $study_id) {
		# this will set the forms and task members
		$s = new Schedule;
		$this->sched = $s->getone(array('task_id' => $task_id, 'study_id' => $study_id));
		$this->parseforms($task_id);
		return $this->formstring2xml();
	}
	
	public function formstring2xml () {
		
		$xml = <<<XML
<?xml version="1.0" encoding="UTF-8"?>
<task>
    <task_id>{$task_id}</task_id>
    <task_name>{$this->task['task_title']}</task_name>
    <notes>{$this->task['task_notes']}</notes>

XML;
		if (is_array($this->sched)) {
			$xml .= <<<XML
    <schedule>
        <start>{$sched['startdate']}</start>
        <end>{$sched['enddate']}</end>
        <daysofweek>{$sched['daysofweek']}</daysofweek>
        <timesofday>{$sched['timesofday']}</timesofday>
    </schedule>

XML;
		}
		$num = 0;
		if (is_array($this->forms)) {
			foreach ($this->forms as $form) {
				$xml .= <<<XML
    <form>

XML;
				foreach ($form as $idata) {
					$num++;
					$instruction = trim($idata['instruction']);
					$xml .= <<<XML
        <taskitem>
            <taskitem_id>$num</taskitem_id>
            <instruction>$instruction</instruction>
            <format>{$idata['format']}</format>
        </taskitem>

XML;
				}
				$xml .= <<<XML
    </form>

XML;
			}
		}
			$xml .= <<<XML
</task>

XML;
		return $xml;
	}

	public function forms2html($task_id,$study_id) {
		try {
			$this->parseforms($task_id,'html');
			if (!is_array($this->forms)) 
				throw new Exception("No forms!");
			return $this->formstring2html();
		} catch (Exception $e) {
			return "ERROR : {$e->getMessage()}";
		}
	}

	public function formstring2html() {
		
		$formnum = 1;
		if (is_array($this->forms)) {
			foreach ($this->forms as $form) {
				$html .= <<<HTML
    <div id="form$formnum" class="form">

HTML;
				foreach ($form as $idata) {
					$instruction = trim($idata['instruction']);
					if ($instruction) 
						$instruction = "<h4 class=\"instruction\">$instruction</h4>";
					if ($idata['format']) {
						$html .= <<<HTML
        <div class="taskitem">
	    $instruction
            <div class="format">{$idata['format']}</div>
        </div>

HTML;
					} else {
						$html .= <<<HTML
        <div class="taskitem">
            <h4 class="instruction">$instruction</h4>
        </div>

HTML;
					}
				}
				$formnum++;
				$html .= <<<HTML
    </div>
<!-- split -->

HTML;
			}
		}
		return $html;
	}
}

/* 
 * relation classes
 */
class Research extends Relation {
	public function __construct() {
		global $DRDAT, $tables;
		parent::__construct($DRDAT,$tables['research'], 'research');
	}
}

class Schedule extends Relation {
	public function __construct() {
		global $DRDAT, $tables;
		parent::__construct($DRDAT,$tables['schedule'], 'schedule');
	}

	public function tasklist($_id,$_extra=null) {
		try {
			$empty = false;
			if (!Check::digits($_id, $empty)) {
				if (!Check::isemail($_id, $empty)) throw new Exception("bad email!");
				if (!Check::ismd5($_extra)) throw new Exception("bad pw!");
				$where = "where participant.email='%s' and participant.password='%s' ";
			} else {
				$where = "where study.study_id=%u ";
			}
			$this->run(
				"select distinct task.task_id, task.task_title, schedule.* ".
				"from task join schedule using (task_id) ".
				"join study using (study_id) ".
				"join enrollment using (study_id) ".
				"join participant using (participant_id) ".
				$where.
				"and study.startdate <= schedule.startdate ".
				"and study.enddate >= schedule.enddate ".
				"and schedule.active = 1 ".
				"order by task.task_id",
				$_id,$_extra
			);
			return $this->resultarray();

		} catch (Exception $e) {
			$this->err($e);
			return false;
		}
	}

	/**
	 * what gets sent to the phone when a request for 
	 * the tasks for a participant is made
	 */
	public function tasklist2xml($study_id) {
		if (($tasklist = $this->tasklist($study_id)) === false)
			die($this->err());
			return $this->tasks2xml($tasklist);
	}
	
	public function tasks2xml($tasklist) {
		foreach ($tasklist as $task) {
			$study_id = $task['study_id'];
			$xml .= <<<XML
    <task>
        <task_id>{$task['task_id']}</task_id>
        <task_name>{$task['task_title']}</task_name>
        <schedule>
            <start>{$task['startdate']}</start>
            <end>{$task['enddate']}</end>
            <daysofweek>{$task['daysofweek']}</daysofweek>
            <timesofday>{$task['timesofday']}</timesofday>
        </schedule>
    </task>

XML;
		}
		$xml = <<<XML
<?xml version="1.0" encoding="UTF-8"?>
<tasklist>
    <study_id>$study_id</study_id>
$xml
</tasklist>

XML;
		return $xml;
	}
}

class Enrollment extends Relation {
	public function __construct() {
		global $DRDAT, $tables;
		parent::__construct($DRDAT,$tables['enrollment'], 'enrollment');
	}

}

