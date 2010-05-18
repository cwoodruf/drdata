{*
---------------------------------------------------------------
Author Cal Woodruff cwoodruf@gmail.com
Licensed under the Perl Artistic License version 2.0
http://www.perlfoundation.org/attachment/legal/artistic-2_0.txt
---------------------------------------------------------------
*}
{part part_id=$part_id}
<a href="index.php">Home</a>
<p>
<a href="index.php?action=Participants&study_id={$study_id}">
Return to enroll participants page</a>

<h3>
{if $part_id}
Edit info for {$part.firstname} {$part.lastname}
{else}
New Participant
{/if}
</h3>

<form action="index.php" name="partform" id="partform" method="post">
<input type=hidden name=participant_id value="{$part_id}">
<input type=hidden name=study_id value="{$study_id}">
<table cellpadding=5 cellspacing=0 border=0 width={$tbwidth}>

{* use the schema data to help us build a form *}
{foreach from=$schema.participant key=field item=fdata}
{if $field == 'password'}{php}continue;{/php}{/if}
<tr>
<td valign=top>{$field|replace:'_':' '|capitalize}: </td>
<td>
{inputwidget field=$field fdata=$fdata input=$part}
</td>
</tr>

{/foreach}

<tr><td colspan=2 align=right><input type=submit name=action value="Save Participant"></td></tr>
</table>
</form>
<script>document.partform.email.focus();</script>

