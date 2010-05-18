{*
---------------------------------------------------------------
Author Cal Woodruff cwoodruf@gmail.com
Licensed under the Perl Artistic License version 2.0
http://www.perlfoundation.org/attachment/legal/artistic-2_0.txt
---------------------------------------------------------------
*}
<head>
<title>test smi: {action}</title>
<link rel="stylesheet" type="text/css" href="css/main.css">
<script type="text/javascript" src="js/jquery.js"></script>
{literal}
<script type="text/javascript">
$(document).ready(function () {
});
</script>
{/literal}
</head>
<body>
<center>
<h3>
DRDAT Study Management Interface

{if $user->valid() and $action != 'Log Out'}
(<a href="index.php?action=Log+Out" class="editlink i">Log Out</a>)
{elseif !$user->valid() and $action != '' and $action != 'Log In'}
(<a href="index.php" class="editlink i">Log In Form</a>)
{/if}

</h3>
