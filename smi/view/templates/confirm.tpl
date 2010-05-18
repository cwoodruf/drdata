{*
---------------------------------------------------------------
Author Cal Woodruff cwoodruf@gmail.com
Licensed under the Perl Artistic License version 2.0
http://www.perlfoundation.org/attachment/legal/artistic-2_0.txt
---------------------------------------------------------------
*}
<form action="index.php" method="post">
<a href="{$backurl}">Go Back</a>
<h4>{$question}</h4>
<p>

{foreach from=$data key=field item=value}
<input type="hidden" name="{$field}" value="{$value}">
{/foreach}

<input type="submit" name="action" value="{$action}">
</form>
