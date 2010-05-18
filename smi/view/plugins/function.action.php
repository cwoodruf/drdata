<?php
/*
---------------------------------------------------------------
Author Cal Woodruff cwoodruf@gmail.com
Licensed under the Perl Artistic License version 2.0
http://www.perlfoundation.org/attachment/legal/artistic-2_0.txt
---------------------------------------------------------------
*/
/**
 * show sanitized action name
 */
function smarty_function_action($params,&$smarty) {
	$action = SMIAction::action();
	if ($action == 'Log In') {
		if ($_SESSION['user']) {
			return 'Home';
		}
	}
	return htmlentities(substr($action,0,VIEW_MAXFIELDSIZE));
}

