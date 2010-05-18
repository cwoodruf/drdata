<?php
/**
 * extract text from a page that is about to be output
 */
function smarty_outputfilter_extract($output,&$smarty) {
	global $texts, $textcount;
	$textcount = 0;
	$texts = array();

	$shell = preg_replace_callback(
			'#(<[^>]*>)([^<>]+)(</[^>]*>)#s', 
			'extract_text', 
			$output
		);
	$extracted = var_export($texts,true);
	
	file_put_contents(
		"view/templates/{$smarty->resource_name}.texts", 
		"<?php\n\$texts = ".var_export($texts,true)
	);
	file_put_contents(
		"view/templates/{$smarty->resource_name}.shell", 
		$shell
	);

	return <<<HTML

<!-- resource {$smarty->resource_name} start -->

$output

<!-- resource {$smarty->resource_name} end -->
<!--
$extracted
--> 

HTML;

}

function extract_text($matches) {
	global $texts, $textcount;
	$trimmed = trim($matches[2]);
	if ($trimmed and !preg_match('#script#',$matches[3])) {
		$texts[$textcount] = $matches[2];
		$out = $matches[1]."{\$texts[$textcount]}".$matches[2];
		$textcount++;
	}
	return $out; 
}
