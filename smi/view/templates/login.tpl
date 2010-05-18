{*
---------------------------------------------------------------
Author Cal Woodruff cwoodruf@gmail.com
Licensed under the Perl Artistic License version 2.0
http://www.perlfoundation.org/attachment/legal/artistic-2_0.txt
---------------------------------------------------------------
*}
{assign var='tbwidth' value='400'}
{assign var='colwidth' value='40%'}

{capture name=signuplink assign=signuplink}{literal}
<a href="javascript: void(0);" 
   onclick="
if ($('#signupform').hasClass('hidden')) {
   $('#signupform').show().removeClass('hidden');
   $(this).hide();
} else {
   $('#signupform').hide().addClass('hidden');
}
" class="editlink i"
>Sign Up</a>
{/literal}{/capture}

<form action="index.php" id="loginform" name="loginform" method="post">
<h3>Log In</h3>
{login_error}
<table border=0 cellpadding=5 cellspacing=0 width={$tbwidth}>
<tr><td width="{$colwidth}">Email: </td><td><input id="email" name="email" size=30></td></tr>
<tr><td>Password: </td><td><input type="password" name="password" size=30></td></tr>
<tr><td colspan=2 align="right">
{$signuplink} &nbsp;&nbsp;
<input type="submit" name="action" value="Log In"></td></tr>
</table>
</form>
<script>document.loginform.email.focus();</script>


<form action="index.php" id="signupform" method="post" class="hidden">
<h3>Sign Up</h3>
<table border=0 cellpadding=5 cellspacing=0 width={$tbwidth}>
<tr><td width="{$colwidth}">Email: </td><td><input id="email" name="email" size=30></td></tr>
<tr><td>Confirm Email: </td><td><input id="email" name="emailconfirm" size=30></td></tr>
<tr><td>Password: </td><td><input type="password" name="password" size=30></td></tr>
<tr><td>Confirm Password: </td><td><input type="password" name="passwordconfirm" size=30></td></tr>
<tr><td colspan=2 align="right"><input type="submit" name="action" value="Sign Up"></td></tr>
</table>
</form>
