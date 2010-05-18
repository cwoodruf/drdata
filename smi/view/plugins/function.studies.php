<?php
/*
---------------------------------------------------------------
Author Cal Woodruff cwoodruf@gmail.com
Licensed under the Perl Artistic License version 2.0
http://www.perlfoundation.org/attachment/legal/artistic-2_0.txt
---------------------------------------------------------------
*/
/**
 * show a list of study links
 */
function smarty_function_studies($params,&$smarty) {
	$s = new Study;
	$smarty->assign(
		'studies', 
		$s->studies(
			$_SESSION['user']['researcher_id'],
			$params['all'] ? 1 : 0
		)
	);
}

