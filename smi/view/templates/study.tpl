{*
---------------------------------------------------------------
Author Cal Woodruff cwoodruf@gmail.com
Licensed under the Perl Artistic License version 2.0
http://www.perlfoundation.org/attachment/legal/artistic-2_0.txt
---------------------------------------------------------------
*}
{study study_id=$study_id}
<a href="index.php">Home</a>

<h4>
{if !isset($study_id)}
New Study
{else}
Study: {$study.study_title} &nbsp;&nbsp; <span class="i">({$study.startdate} to {$study.enddate})</span>
{/if}
</h4>

<table cellpadding=5 cellspacing=0 border=0 class="nowidth nobgcolor">
<tr valign=top><td>

<form action="index.php" name="studyform" id="studyform" method="post">
<input type=hidden name=study_id value="{$study_id}">
<table cellpadding=5 cellspacing=0 border=0 class="nowidth">

{* use the schema data to help us build a form *}
{foreach from=$schema.study key=field item=fdata}
<tr>
<td valign=top>{$field|replace:'_':' '|capitalize}: </td>
<td>
{inputwidget field=$field fdata=$fdata input=$study}
</td>
</tr>

{/foreach}

<tr><td colspan=2 align=right><input type=submit name=action value="Save Study"></td></tr>
</table>
</form>
<script>document.studyform.study_title.focus();</script>

</td><td>

{if $study_id}
<a name="participants">
<h4><a href="index.php?action=Participants&study_id={$study_id}" 
       class="editlink b">Enroll Participants ({$participants} enrolled)</a></h4>

<a name="tasks">
<h4>Tasks 
- 
<a href="index.php?action=Create+Task&study_id={$study_id}" 
   class="editlink i">Create a task</a>
- 
<a href="index.php?action=Preview+Tasklist&study_id={$study_id}" 
   class="editlink i">Preview task list xml</a>
-
{if $all}
{assign var=showall value=0}
{assign var=showlink value="Hide inactive tasks"}
{else}
{assign var=showall value=1}
{assign var=showlink value="Show inactive tasks"}
{/if}
<a href="index.php?action=Show+Study&study_id={$study_id}&all={$showall}"
   class="editlink i">{$showlink}</a>
</h4>
{tasks study_id=$study_id all=$all}

<table class="nobgcolor"><tr align=left><td>
<ul class="tasklist">
{foreach from=$tasks key=num item=tdata}

{if $tdata.task_id}

{if $tdata.active}
{assign var=activeclass value=active}
{else}
{assign var=activeclass value=inactive}
{/if}

<li class="{$activeclass}">
<nobr>
<span class="{$activeclass}">

<a href="index.php?action=Show+Task&task_id={$tdata.task_id}&study_id={$study_id}" class="editlink">
{$tdata.task_title}</a> 
&nbsp;&gt;&nbsp;  
<a href="index.php?action=Confirm+Copy+Task&study_id={$study_id}&task_id={$tdata.task_id}"
   class="editlink i">copy</a>
&nbsp;  
{if $tdata.active}
<a href="index.php?action=Confirm+Remove+Task&active=0&study_id={$study_id}&task_id={$tdata.task_id}" 
   class="editlink i">deactivate</a>
{else}
<a href="index.php?action=Confirm+Activate+Task&active=1&study_id={$study_id}&task_id={$tdata.task_id}" 
   class="editlink i">activate</a>
{/if}
{if $tdata.latest_update}
&nbsp;  
<a href="index.php?action=Download+Data&study_id={$study_id}&task_id={$tdata.task_id}"
   class="editlink i">download data</a>
<span class="editlink i">updated {$tdata.latest_update|regex_replace:'# .*#':''}
{/if}

</span>
</nobr>
{/if}

{/foreach}
</ul>
</td></tr></table>

{/if}
</td></tr>
</table>


