{*
---------------------------------------------------------------
Author Cal Woodruff cwoodruf@gmail.com
Licensed under the Perl Artistic License version 2.0
http://www.perlfoundation.org/attachment/legal/artistic-2_0.txt
---------------------------------------------------------------
*}
{task task_id=$task_id}
<a href="index.php">Home</a>
&nbsp;-&nbsp;
<a href="index.php?action=Show+Task&task_id={$task_id}&study_id={$study_id}">
Return to task {$task.task_title}</a>
<p>
<form action="index.php" id="formform" method="post">
<input type=hidden name=task_id value="{$task_id}">
<input type=hidden name=study_id value="{$study_id}">

<h3>Create forms for task {$task.task_title}</h3>

<table cellpadding=5 cellspacing=0 border=0>
<tr><td>
<h4>Instructions</h4>
</td></tr>
<tr><td>
{include file=../../markup.php}
<h4>Forms
{if $task.formtext}
- <a href="index.php?action=Preview+Forms&study_id={$study_id}&task_id={$task_id}"
     class="editlink i">Preview</a>
{/if}
</h4>
</td></tr>
<tr><td align=right>

{if $task.forms_locked}
<b>Forms locked!</b>
<input type=submit name=action value="Unlock">

{else}
<input type=submit name=action value="Save Forms">

{/if}

</td></tr>
<tr><td>
<textarea name=formtext rows=40 cols=80>{$task.formtext}</textarea>
</td></tr>
</table>
</form>
