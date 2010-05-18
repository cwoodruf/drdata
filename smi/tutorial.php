<!--
---------------------------------------------------------------
Author Cal Woodruff cwoodruf@gmail.com
Licensed under the Perl Artistic License version 2.0
http://www.perlfoundation.org/attachment/legal/artistic-2_0.txt
---------------------------------------------------------------
-->
<html>
<head>
<link rel=stylesheet type=text/css href=css/main.css>
<link rel=stylesheet type=text/css href=css/tutorial.css>
<title>DRDAT Tutorial</title>
</head>
<body>
<center>
<h3>Distributed Real-time Data Acquision Toolkit (DRDAT) Tutorial</h3>
<table cellpadding=5 cellspacing=0 border=0>
<tr>
<td>
DRDAT is a set of tools that makes it easy for you to design and deploy research studies that use
mobile phones as input devices. It is well suited for types of self-reporting research where a
study participant would need to enter data in the field. 
<p>
One major advantage of DRDAT is that any data entered by a study participant will automatically 
be uploaded to the Study Management Interface (SMI) web server at the time the data was entered. 
You can track data in real time while the study is ongoing. 
<p>
In addition you can set a schedule for when specific tasks need to be done and the study participants 
will be automatically reminded by the mobile device when they need to complete those tasks.

<h4>Get started: create a study</h4>
A study in DRDAT is a container for a set of tasks done by a group of study participants. 
To create a new study click <b>Create a Study</b> on your DRDAT home page. 
<p>
A study has a title, description and a start and end date. Make sure to enter valid start and end 
dates as these determine when participants can submit data for a given study.

<h4>Next step: create tasks</h4>
Once you have saved your new study you will need to add tasks and participants. A task is a set of
data entry forms that can have an associated schedule. The schedule is only a reminder: it cannot
enforce when or whether a participant does a task. The data entry forms are simple html forms that 
you can create either directly in html or using a simple form design language. 
<p>
To create a new task click the <b>Create a task</b> link in the study page for your new study. Enter
the title for the task (visible to participants on their phones) and notes (not visible) and click
<b>Save task</b>. At this point you should see the task and a schedule form. The schedule determines when
a study participant would be reminded to do this task. 
<p>
Schedules are not required but are recommended. Even if it is not necessary for a task to be done
at a specific time having a reminder during the day may make it easier for the participant to 
remember to do it. You can also elect to remind a participant only on certain days of the week. So
if you have a task that only needs to be done once per week you can set the reminder for a specific
week day rather than have the reminder happen every day.

<h4>Task forms</h4>
Tasks usually have data entry forms. You can use our simple 
<i>
<?php
print $markuplink = <<<HTML
<a href="javascript: void(0)" 
   onClick="window.open('markup.php','markup','toolbars=no,scrollbars=no,width=700,height=400,menubar=no'); 
            return false;">mark up language</a>
HTML;
?>
</i> to make data entry forms.
<p>
Task do not have to  have forms and task forms do not have to include data entry elements and can simply be 
more detailed instructions to the end user. However creating a set of forms in DRDAT is very flexible and 
easy to learn. 
<p>
To create a set of forms for a task go to the task page (click on the link for the task from the study
page) and click on the <b>Edit forms</b> link. In the form editor try entering:
<p>
<table class="nobgcolor" cellpadding=5 cellspacing=0 border=0>
<tr><td>
<pre>
# a comment
i: first instruction!
w: checkbox
o: first checkbox
o: second checkbox
i: second instruction
--
i: third instruction on a new form
w: text
i: multiline text
w: text 30,4
--
i: third form
w: dropdown
o: first option
o: second option
</pre>
</td></tr>
</table>
<p> 
Click "Save forms" to preview the forms made.
<p>
Remember that the size of a screen on a mobile device is usually relatively small so the forms you
design will need to be smaller than a web form you might design for a desktop browser application.
<p>
You can also make forms directly in html. For the forms to work you will need to follow the same
naming convention as that used by DRDAT for identifying data and instruction elements. Try making 
and previewing a few forms to get a better idea of how instructions and data inputs are named by DRDAT.
<p>
<p>
If you choose to work with html do not use html <b>&lt;form&gt;</b> elements in your forms as these 
will hide data from the data entry app. To identify where a form should finish use <b>--</b> 
or <b>&lt;!-- split --&gt;</b>. Even if instruction elements are blank there should be a hidden field 
for that instruction. Because of security limitations in android it is not possible to include external
style sheets and scripts with your html.

<h4>Form locking and data integrity</h4>
By default when DRDAT's SMI receives data for a certain task that task is flagged and its forms are locked.
In other words once someone in your study sends you some data the forms for that task cannot be changed.
Rather, they <i>should not</i> be changed. However, it may be the case that you need to change something in
a form even after the study has commenced. The best practice in this case is to make a copy of that task 
in the study and deactivate the original locked task. You can then change the new task's form and have a 
clear indication in your collected data when the task was changed. To copy a task go to the study page for 
that task and click <b>copy</b> next to the task.

<h4>Next step: enter participant information</h4>
For a participant (or researcher) to use DRDAT on a mobile device they must be enrolled in a study. 
To enroll yourself or another participant in a study go to the study page for your new study and click
<b>Enroll Participants</b>.
<p>
In the participants page click <b>Add a study participant</b> to add someone to the study. Enter the 
email address and first name at least to make a new study participant. Click <b>Save participant</b> to save 
this information. Click <b>Return to enroll participants page</b>. You should see the participant listed. 
<p>
But we aren't done yet! For the participant to access data from a mobile device you need to set a
password for that participant. Click on <b>add password</b> to make a new password for that participant. 
Participants cannot change their passwords.

<h4>Last step: test on a mobile device</h4>
You will need to download <a href="http://code.google.com/p/drdat/downloads/list">DRDAT app</a> to an android 
mobile device. These applications currently require android 2.1 (api version 7). You may wish to install
the <a href="http://developer.android.com/sdk/index.html">android system development kit</a> to try the 
apps on your pc with the android emulator.

<h4>Downloading tasks</h4>
After downloading and installing both of the DRDAT mobile apps you will want to download tasks to the 
mobile device.  Start the DRDAT task manager then select <b>View Tasks</b> from the main menu to 
update tasks for a participant. Next enter a valid participant email and password and press 
<b>View Tasks</b> to download the tasks for the participant. When the download is complete you 
should see a list of tasks.  You can press on a task in this list to start it.

<h4>Starting a task</h4>
Participants can start a task in 2 ways. Firstly they can start a task from a notification on the 
mobile device based on the schedule you entered for that task. Secondly they can start the data
entry app and select from a list as described above.

<h4>Data uploads</h4>
DRDAT's form entry tool will try and send data as soon as the participant has saved it. However, if 
this fails it will try to send data every hour until the data is sent. You can also upload data 
manually from the DRDAT Task Manager app. 

<h4>Wiping data</h4>
If you need to wipe participant data you can do that from the DRDAT Task Manager. Scroll down to the 
bottom of the menu and press <b>Delete tasks and logins</b>. Also be sure to upload any remaining 
participant data and use <b>Delete uploaded data</b> to delete it on the mobile device.
<p>
You can only delete entered data once it has been successfully sent back to the SMI. 
</td>
</tr>
</table>
</center>
</body>
</html>
