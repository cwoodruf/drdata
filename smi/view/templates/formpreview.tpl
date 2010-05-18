{*
---------------------------------------------------------------
Author Cal Woodruff cwoodruf@gmail.com
Licensed under the Perl Artistic License version 2.0
http://www.perlfoundation.org/attachment/legal/artistic-2_0.txt
---------------------------------------------------------------
*}
<a href="index.php">Home</a>
&nbsp;-&nbsp;
<a href="index.php?action=Edit+Forms&study_id={$study_id}&task_id={$task_id}">
Back to form editor</a>

<h4>Forms as they might appear on a phone</h4>
<table><tr><td>
<center>
{formhtml study_id=$study_id task_id=$task_id style=mobile width=320}
</center>
</td></tr></table>
<p>
<h4>Raw html as it would be sent to a phone</h4>
<table><tr><td>
<pre>
{formhtml study_id=$study_id task_id=$task_id style=raw}
</pre>
</td></tr></table>
