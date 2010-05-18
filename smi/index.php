<?php
/*
---------------------------------------------------------------
Author Cal Woodruff cwoodruf.com
Licensed under the Perl Artistic License version 2.0
http://www.perlfoundation.org/attachment/legal/artistic-2_0.txt
---------------------------------------------------------------
*/

/**
 * main entry point for all request processing
 */

# if __SMI__ is not false then its not defined - php defines unknown strings to true (ps: gah!)
define('__SMI__',false);
session_start();
require_once('lib/includes.php');

$user = new Login;
$action = new SMIAction;

View::assign('user',$user);
View::assign('action',$action->get());

$contenttype = 'text-html';
if (!$action->unblocked() and !$user->valid()) {
	View::head();
	View::display('login.tpl');
	exit();
} else {
	$template = $action->process();
	View::head($contenttype);
	View::display($template);
}

View::foot($contenttype);

