{*
---------------------------------------------------------------
Author Cal Woodruff cwoodruf@gmail.com
Licensed under the Perl Artistic License version 2.0
http://www.perlfoundation.org/attachment/legal/artistic-2_0.txt
---------------------------------------------------------------
*}
<a href="index.php">Home</a>
<h3>Edit my password</h3>
<form action="index.php" name="passform" method="post">
<input type="hidden" name="researcher_id" value="{$researcher_id}">
<table cellpadding=5 cellspacing=0 border=0>
<tr><td>Password:</td><td><input type="password" name="password" value="" size=30></td></tr>
<tr><td>Confirm Password:</td><td><input type="password" name="confirmpassword" value="" size=30></td></tr>
<tr><td colspan=2 align=right><input type="submit" name="action" value="Save Researcher Password"></td></tr>
</table>
</form>
<script>document.passform.password.focus();</script>
