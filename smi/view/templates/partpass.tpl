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
<a href="index.php?action=Participants&study_id={$study_id}">Back to participants page</a>
<h3>Edit password for {$part.firstname} {$part.lastname} &lt;{$part.email}&gt;</h3>
<form action="index.php" name="passform" method="post">
<input type="hidden" name="study_id" value="{$study_id}">
<input type="hidden" name="participant_id" value="{$part_id}">
<table cellpadding=5 cellspacing=0 border=0>
<tr><td>Password:</td><td><input type="password" name="password" value="" size=30></td></tr>
<tr><td>Confirm Password:</td><td><input type="password" name="confirmpassword" value="" size=30></td></tr>
<tr><td colspan=2 align=right><input type="submit" name="action" value="Save Participant Password"></td></tr>
</table>
</form>
<script>document.passform.password.focus();</script>
