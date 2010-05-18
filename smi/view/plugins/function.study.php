<?php
/*
---------------------------------------------------------------
Author Cal Woodruff cwoodruf@gmail.com
Licensed under the Perl Artistic License version 2.0
http://www.perlfoundation.org/attachment/legal/artistic-2_0.txt
---------------------------------------------------------------
*/
/**
 * get data for an individual study based on 
 * the user's researcher_id and a supplied study_id
 * creates $study smarty variable
 */
function smarty_function_study($params,&$smarty) {
	if (!Check::digits($params['study_id'])) {
		$smarty->assign('study',array());
		return;
	}
	$s = new Study;
	$study = $s->study(
		$_SESSION['user']['researcher_id'], 
		$params['study_id']
	);
	$p = new Enrollment;
	$participants = $p->howmany(
		array(
			" where study_id=%u and active>0 ",
			$params['study_id']
		)
	);
	$smarty->assign('study',$study);
	$smarty->assign('participants',$participants);
}

