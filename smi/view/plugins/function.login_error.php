<?php
/**
 * check if someone tried to log in and couldn't
 */
function smarty_function_login_error($params,&$smarty) {
	global $user;
	if (SMIAction::action() == 'Log In' && !$user->valid()) return 'Bad login or password!';
}

