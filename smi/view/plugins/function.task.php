<?php
/*
---------------------------------------------------------------
Author Cal Woodruff cwoodruf@gmail.com
Licensed under the Perl Artistic License version 2.0
http://www.perlfoundation.org/attachment/legal/artistic-2_0.txt
---------------------------------------------------------------
*/
/**
 * get task information for the specified task
 */
function smarty_function_task($params,&$smarty) {
	$t = new Task;
	if (!Check::digits($params['task_id'],($empty=false))) return;
	$smarty->assign('task',$t->getone($params['task_id']));
}

