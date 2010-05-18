{*
---------------------------------------------------------------
Author Cal Woodruff cwoodruf@gmail.com
Licensed under the Perl Artistic License version 2.0
http://www.perlfoundation.org/attachment/legal/artistic-2_0.txt
---------------------------------------------------------------
*}
{* check if they just signed up *}
{if $smarty.session.user.firstname == ''}

{include file=researcher.tpl}

{else}

<a href="javascript:void(0);" 
   onClick="window.open('tutorial.php','tutorial','toolbars=no,scrollbars=yes,menubar=no,width=700,height=700'); return false;" 
   class="editlink">Tutorial</a>

<h4>Welcome {$smarty.session.user.firstname|capitalize} 
 &nbsp;/&nbsp; <a href="index.php?action=Edit+Researcher" class="editlink i">Edit profile</a> 
 &nbsp;/&nbsp; <a href="index.php?action=Researcher+Password" class="editlink i">Edit password</a> </h4>
<p>
<h4>My studies - <a href="index.php?action=Create+Study" class="editlink i">Create a study</a> - 
{if $all}
<a href="index.php?all=0" class="editlink i">Active studies only</a>
{else}
<a href="index.php?all=1" class="editlink i">Show all studies</a>
{/if}
</h4>

{studies all=$all}
<table class="nobgcolor"><tr align=left><td>
<ul class="studylist">
{foreach from=$studies key=num item=sdata}

{if $sdata.study_id}

{if $sdata.visible}
{assign var=activeclass value=active}
{assign var=activeaction value='Confirm+Hide+Study&visible=0'}
{assign var=activelink value='hide'}
{else}
{assign var=activeclass value=inactive}
{assign var=activeaction value='Confirm+Activate+Study&visible=1'}
{assign var=activelink value='unhide'}
{/if}

<li class="{$activeclass}">
<span class="{$activeclass}">
<a href="index.php?action=Show+Study&study_id={$sdata.study_id}" class="editlink">
{$sdata.study_title}</a> &nbsp; {$sdata.startdate} to {$sdata.enddate} &nbsp;
<a href="index.php?action={$activeaction}&study_id={$sdata.study_id}" class="editlink i">{$activelink}</a>
</span>
{/if}

{/foreach}
</ul>
</td></tr></table>

{/if}
