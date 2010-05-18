{*
---------------------------------------------------------------
Author Cal Woodruff cwoodruf@gmail.com
Licensed under the Perl Artistic License version 2.0
http://www.perlfoundation.org/attachment/legal/artistic-2_0.txt
---------------------------------------------------------------
*}
<a href="index.php">Home</a>
<p>
<a href="index.php?action=Show+Study&study_id={$study_id}">Return to study</a>

<h3>Task list xml as it would be returned to a phone</h3>
<table class="nobgcolor"><tr><td>
<pre>
{tasklist2xml study_id=$study_id}
</pre>
</td></tr></table>
