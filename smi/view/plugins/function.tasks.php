<?php
/*
---------------------------------------------------------------
Author Cal Woodruff cwoodruf@gmail.com
Licensed under the Perl Artistic License version 2.0
http://www.perlfoundation.org/attachment/legal/artistic-2_0.txt
---------------------------------------------------------------
*/
/**
 * find the tasks for this particular study
 */
function smarty_function_tasks($params,&$smarty) {
	$t = new Task;
	if (!Check::digits($params['study_id'])) return;
	$smarty->assign(
		'tasks',
		$t->tasks(
			$params['study_id'],
			$_SESSION['user']['researcher_id'],
			$params['all'] ? true : false
		)
	);
}

