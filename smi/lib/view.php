<?php
/*
---------------------------------------------------------------
Author Cal Woodruff cwoodruf@gmail.com
Licensed under the Perl Artistic License version 2.0
http://www.perlfoundation.org/attachment/legal/artistic-2_0.txt
---------------------------------------------------------------
*/
# use this for code relating to what an end user would see 
if (__SMI__) die("no direct access.");

define('VIEW_MAXFIELDSIZE', 60);
define('VIEW_MAXROWS', 60);
define('VIEW_MINFIELDSIZE', 5);
define('VIEW_MINROWS', 2);

# see http://smarty.net/ for more information
require_once('.smarty/Smarty.class.php');
$smarty = new Smarty;
$smarty->template_dir = getcwd().'/view/templates';
$smarty->compile_dir = getcwd().'/view/templates_c';
$smarty->plugins_dir[] = getcwd().'/view/plugins';
$smarty->config_dir = getcwd().'/view/configs';

/**
 * convenience class to access smarty outside of view.php or templates
 */
class View {
	public static $smarty;

	public static function init($s) {
		if (is_object($s)) self::$smarty = $s;
	}

	public static function assign($var,$value) {
		self::$smarty->assign($var,$value);
	}

	public static function head($contenttype='text-html') {
		if (!preg_match('#^[\w\-]+$#', $contenttype)) $contenttype = 'text-html';
		self::$smarty->display("header-$contenttype.tpl");
	}

	public static function foot($contenttype='text-html') {
		if (!preg_match('#^[\w\-]+$#', $contenttype)) $contenttype = 'text-html';
		self::$smarty->display("footer-$contenttype.tpl");
	}

	public static function display($template) {
		self::$smarty->display($template);
	} 
}

View::init($smarty);

# this is defined in lib/drdat-schema.php
View::assign('schema', $tables);
View::assign('primary', 'PRIMARY KEY'); # hack so we can use this key in templates




