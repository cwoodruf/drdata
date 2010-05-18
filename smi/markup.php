<!--
---------------------------------------------------------------
Author Cal Woodruff cwoodruf@gmail.com
Licensed under the Perl Artistic License version 2.0
http://www.perlfoundation.org/attachment/legal/artistic-2_0.txt
---------------------------------------------------------------
-->
<link rel=stylesheet type=text/css href=css/main.css>
<table cellpadding=5 cellspacing=0 border=0 align=center style="border: none">
<tr><td>
To be usable on a phone each task must have at least one form. Forms contain
instructions followed by input widgets. An instruction does not have to have an input widget.
<p>
To create the forms for this task enter the question or instructions 
On the following line optionally enter a widget type for data entry
For the checkbox and dropdown widgets you will need to add items for that widget
You may only have one widget per question.
Questions may have multiple lines.
</td></tr>
<tr><td>
<h4>Formatting codes: 
    <i style="font-weight: normal" >codes should be the first characters on the line</i></h4>
<table align=center cellpadding=3 cellspacing=0 border=1 class="nobgcolor">
<th>line starts with</th><th>what that does</th>
<tr><td>#</td><td>text on this line is ignored</td><tr>
<tr><td>--</td><td>starts new form - any following characters are ignored</td></tr>
<tr><td>i:</td><td>starts an instruction or question: instructions can be on multiple lines</td></tr>
<tr><td>w:</td><td>identifies input widget: can be one of none, text, dropdown, checkbox</td></tr>
<tr><td>o:</td><td>add line option: dropdown &amp; checkbox widgets require at least one option</td></tr>
</td></tr>
</table>
</td></tr>
<tr><td>
You may optionally make a text widget into a box by adding columns and rows after "text". For
example <b>w:text 20,4</b> will make a text box with 20 columns and 4 rows.
</td>
</tr>
</table>

