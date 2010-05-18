{*
---------------------------------------------------------------
Author Cal Woodruff cwoodruf@gmail.com
Licensed under the Perl Artistic License version 2.0
http://www.perlfoundation.org/attachment/legal/artistic-2_0.txt
---------------------------------------------------------------
*}
{study study_id=$study_id}
{parts study_id=$study_id all=$all}

<a href="index.php">Home</a>
<p>
<a href="index.php?action=Show+Study&study_id={$study_id}">
Return to study page</a>

<h3>Participant list for {$study.study_title}</h3>


<h4>Currently enrolled
<a href="index.php?action=Show+Participant&study_id={$study_id}" class="editlink i">
Add a study participant</a>
</h4>

{if $all}
<a href="index.php?action=Participants&study_id={$study_id}&all=0">Hide inactive</a>
{else}
<a href="index.php?action=Participants&study_id={$study_id}&all=1">Show all</a>
{/if}

<table cellpadding=5 cellspacing=0 border=0 class="nobgcolor">
<tr><td>
<ul>
{foreach from=$parts key=num item=part}
{assign 
	var=l 
	value="index.php?study_id=`$study_id`&participant_id=`$part.participant_id`"
}

{if $part.active}
{assign var=state value=remove}
{assign var=active value=0}
{assign var=activeclass value=active}
{else}
{assign var=state value=reactivate}
{assign var=active value=1}
{assign var=activeclass value=inactive}
{/if}

<li class="{$activeclass}">
<span class="{$activeclass}">
<a href="{$l}&action=Show+Participant"
       class="editlink b">{$part.firstname} {$part.lastname} <span style="font-weight: normal">(edit)</span></a>
&lt;<a href="mailto:{$part.email}">{$part.email}</a>&gt;

{if $part.password}
{assign var=what value=edit}
{else}
{assign var=what value=add}
{/if}
<span class="editlink i">
&nbsp;/&nbsp; <a href="{$l}&action=Participant+Password" class="editlink i">{$what} password</a>

&nbsp;/&nbsp; 
<a href="{$l}&active={$active}&action=Confirm+Remove+Participant" 
   class="editlink i">{$state}</a>

{if $part.latest_update != ""}
&nbsp;&nbsp;
last update {$part.latest_update|regex_replace:'# .*#':''}
{/if}
</span>
</span>

{/foreach}
</td></tr>
</table>
