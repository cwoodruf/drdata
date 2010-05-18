<?php
/*
---------------------------------------------------------------
Author Cal Woodruff cwoodruf@gmail.com
Licensed under the Perl Artistic License version 2.0
http://www.perlfoundation.org/attachment/legal/artistic-2_0.txt
---------------------------------------------------------------
*/
/**
 * take our raw form data and make an xml representation of it
 * that should be exactly what gets sent to the phones
 */
function smarty_function_formhtml($params,&$smarty) {
	if (!Check::digits($params['task_id'],($empty=false))) return;
	# study_id is needed for the scheduling information for the task
	if (!Check::digits($params['study_id'],($empty=false))) return;
	$t = new Task;
	if ($params['style'] == 'mobile') {
		if ($params['width'] > 0) $width = (int) $params['width'];
		else $width = 400;
		$rawhtml = $t->forms2html($params['task_id'],$params['study_id']);
		$forms = explode('<!-- split -->', $rawhtml);
		$numforms = count($forms) - 1;
		foreach($forms as $block) {
			$f++;
			if ($f > $numforms) break;
			$spacer = "";
			if ($f == 1) {
				$prev = "&lt; prev";
				$next = "<input type=submit value=\"next &gt;\">";
			} else if ($f < $numforms) {
				$prev = "<input type=submit value=\"&lt; prev\">";
				$next = "<input type=submit value=\"next &gt;\">";
			} else {
				$prev = "<input type=submit value=\"&lt; prev\">";
			}
			if ($f == $numforms) {
				$next = "<input type=submit value=\"Save data\">";
				$spacer = "<br><br><br>";
			}
			
			$html .= <<<HTML
<h4>Form $f</h4>
<table cellpadding=2 cellspacing=0 border=1 style="width: $width" width=$width class="nobgcolor">
<tr><td>
$block
<br>
$prev $next
<br><br>
</td></tr>
</table>
$spacer

HTML;
		}
		return $html;
	}
	
	return htmlentities($t->forms2html($params['task_id'],$params['study_id']));
}

