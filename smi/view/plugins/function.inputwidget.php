<?php
/*
---------------------------------------------------------------
Author Cal Woodruff cwoodruf@gmail.com
Licensed under the Perl Artistic License version 2.0
http://www.perlfoundation.org/attachment/legal/artistic-2_0.txt
---------------------------------------------------------------
*/
/**
 * take schema data and make an input widget
 */
function smarty_function_inputwidget($params,&$smarty) {
	# check and grab our data
	if (!is_array($params['fdata'])) return;
	else $fdata = $params['fdata'];

	if (!Check::isvar($params['field'])) return;
	else $field = $params['field'];

	if (!is_array($params['input'])) $input = array();
	else $input = $params['input'];

	# configure some dimensions for the display
	$max = $fdata['size'];
	if ($fdata['size'] > VIEW_MAXFIELDSIZE) $size = VIEW_MAXFIELDSIZE;
	else $size = $fdata['size'];

	if ($fdata['cols'] > VIEW_MAXFIELDSIZE) $cols = VIEW_MAXFIELDSIZE;
	else $cols = $fdata['cols'];
	
	if ($fdata['rows'] > VIEW_MAXROWS) $rows = VIEW_MAXROWS;
	else $rows = $fdata['rows'];

	if ($rows < VIEW_MINROWS) $rows = VIEW_MINROWS;
	if ($cols < VIEW_MINFIELDSIZE) $cols = VIEW_MINFIELDSIZE;

	# squash any wierdness in the data
	$value = htmlentities($input[$field]);
	if ($fdata['key']) {
		if ($value) return "$value <input type=hidden name=\"$field\" value=\"$value\">";
		return "Create new";
	}
	if ($fdata['hide']) {
		if ($value) return "<input type=hidden name=\"$field\" value=\"$value\">";
		return;
	}

	# output a widget
	switch ($fdata['type']) {
		case 'datetime' : 
			if (!$value) $value = date('Y-m-d H:I:S');
		case 'time' : 
			if (!$value) $value = date('H:I:S');
		case 'date' : 
			if (!$value) $value = date('Y-m-d');
		case 'varchar' : 
		case 'int' : 
			$html =  "<input size=\"$size\" maxlength=\"$max\" name=\"$field\" value=\"$value\">";
		break;

		case 'text':
			$html =  "<textarea name=\"$field\" rows=\"$rows\" cols=\"$cols\">$value</textarea>";
		break;

		case 'hidden':
			$html =  "<input type=\"hidden\" name=\"$field\" value=\"$value\">";
		break;

		case 'password':
			$html =  "<input type=\"password\" size=\"$size\" maxlength=\"$max\" ".
				"name=\"$field\" value=\"$value\">";
		break;

		case 'timestamp':
		case 'none':
			$html = $value;
		break;
	}
	if ($fdata['comment']) $html .= $fdata['comment'];
	return $html;
}

