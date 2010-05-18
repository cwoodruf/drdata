{*
---------------------------------------------------------------
Author Cal Woodruff cwoodruf@gmail.com
Licensed under the Perl Artistic License version 2.0
http://www.perlfoundation.org/attachment/legal/artistic-2_0.txt
---------------------------------------------------------------
*}
{task task_id=$task_id}
{if $task.forms_locked}{assign var=locked value="(Forms locked)"}{/if}

<a href="index.php">Home</a>

{if $study_id}
{study study_id=$study_id}
<p>
<a href="index.php?action=Show+Study&study_id={$study_id}">Back to study {$study.study_title}</a>
{/if}

<h4>
{if !isset($task_id)}
New Task 
{else}
Task: {$task.task_title}
- <a href="index.php?action=Edit+Forms&task_id={$task_id}&study_id={$study_id}" 
     class="editlink i">Edit forms {$locked}</a>
{/if}
</h4>

<form action="index.php" name="taskform" id="taskform" method="post">
<input type=hidden name=task_id value="{$task_id}">
{if $study_id}
<input type=hidden name=study_id value="{$study_id}">
<input type=hidden name=startdate value="{$study.startdate}">
<input type=hidden name=enddate value="{$study.enddate}">
{/if}
<table cellpadding=5 cellspacing=0 border=0 width={$tbwidth}>

{* use the schema data to help us build a form *}
{foreach from=$schema.task key=field item=fdata}
{if $fdata.type}
<tr>
<td valign=top>{$field|replace:'_':' '|capitalize}: </td>
<td>
{inputwidget field=$field fdata=$fdata input=$task}
</td>
</tr>
{/if}

{/foreach}

<tr><td colspan=2 align=right><input type=submit name=action value="Save Task"></td></tr>
</table>
</form>
<script>document.taskform.task_title.focus();</script>

{if $task_id and $study_id}

<a name=schedule>
<h4>Schedule</h4>
{schedule task_id=$task_id study_id=$study_id}

<form action="index.php#schedule" name="schedform" id="schedform" method="post">
<table cellpadding=5 cellspacing=0 border=0 width={$tbwidth}>

{foreach from=$schema.schedule key=field item=fdata}
{if $field==$primary}{php}continue;{/php}{/if}
{if $fdata.hide}
{inputwidget field=$field fdata=$fdata input=$schedule}
{else}
<tr>
<td valign=top>{$field|replace:'_':' '|capitalize}: </td>
<td>
{inputwidget field=$field fdata=$fdata input=$schedule}
</td>
</tr>
{/if}
{/foreach}

<tr><td colspan=2 align=right><input type=submit name=action value="Save Schedule"></td></tr>
</table>
</form>

{/if}

