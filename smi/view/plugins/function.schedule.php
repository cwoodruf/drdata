<?php
/*
---------------------------------------------------------------
Author Cal Woodruff cwoodruf@gmail.com
Licensed under the Perl Artistic License version 2.0
http://www.perlfoundation.org/attachment/legal/artistic-2_0.txt
---------------------------------------------------------------
*/
/**
 * grab the schedule for a task in a study
 */
function smarty_function_schedule($params,&$smarty) {
	if (!Check::digits($params['study_id'])) return;
	if (!Check::digits($params['task_id'])) return;
	$s = new Schedule;
	$smarty->assign('schedule',
		$s->getone(
			array(
				'study_id' => $params['study_id'],
				'task_id' => $params['task_id'],
			)
		)
	);
}

