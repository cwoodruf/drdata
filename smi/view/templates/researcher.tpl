{*
---------------------------------------------------------------
Author Cal Woodruff cwoodruf@gmail.com
Licensed under the Perl Artistic License version 2.0
http://www.perlfoundation.org/attachment/legal/artistic-2_0.txt
---------------------------------------------------------------
*}
{assign var=user value=$smarty.session.user}
{assign var=tbwidth value=600}
{assign var=colwidth value='20%'}
{assign var=inputsize value=60} 

{if $user.firstname != ''}
<a href="index.php">Home</a>

{/if}

<form action="index.php" name="researcherform"  method=post>
<h3>Researcher profile</h3>
<table cellpadding=5 cellspacing=0 border=0 width="{$tbwidth}">
<tr><td width="{$colwidth}">First name: </td>
    <td><input name="firstname" size="{$inputsize}" value="{$user.firstname}">
</td></tr>
<tr><td width="{$colwidth}">Last name: </td>
    <td><input name="lastname" size="{$inputsize}" value="{$user.lastname}">
</td></tr>
<tr><td width="{$colwidth}">Institution: </td>
    <td><input name="institution" size="{$inputsize}" value="{$user.institution}">
</td></tr>
<tr><td width="{$colwidth}">Position: </td>
    <td><input name="position" size="{$inputsize}" value="{$user.position}">
</td></tr>
<tr><td width="{$colwidth}">Email: </td>
    <td><input name="email" size="{$inputsize}" value="{$user.email}">
</td></tr>
<tr><td width="{$colwidth}">Phone: </td>
    <td><input name="phone" size="{$inputsize}" value="{$user.phone}">
</td></tr>
<tr><td colspan=2 align=right>
    <input type=submit name=action value="Save Researcher Profile">
</td></tr>
</table>
</form>
<script>document.researcherform.firstname.focus();</script>
