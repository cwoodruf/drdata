/**
 * 
 */
package com.google.android.drdat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Log in form for participants. On successful log in will either
 * launch the DrdatListTasks activity so we can pick a task to do or
 * start DrdatForms' activity with a task we've been given. The task_id
 * etc are provided in the intent extras when DrdatGUI is launched from
 * DrdatCL or a notification.
 * 
 * @author cal
 *
 */
public class PartLogin extends Activity {
	private final String LOG_TAG = "DRDAT NOTE LOGIN";
	private EditText emailView;
	private EditText passwordView;
	private Button login;
	private PartLogin me;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
        setContentView(R.layout.partlogin);
        
        me = this;        
        emailView = (EditText) findViewById(R.id.PartLoginEmail);
        emailView.setText(Login.getLastEmail());
        
        passwordView = (EditText) findViewById(R.id.PartLoginPassword);
        passwordView.setText(Login.getLastPassword());
        
        login = (Button) findViewById(R.id.PartLoginButton);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	String email = emailView.getText().toString();
            	String password = passwordView.getText().toString();
            	Login login = new Login(me,email,password);
            	
            	// these extras would come from a notification
            	Bundle extras = me.getIntent().getExtras(); 
            	int study_id = extras.getInt("study_id");
            	int task_id = extras.getInt("task_id");
            	Log.d(LOG_TAG, "checking "+email+"/"+password+" study_id "+study_id+" task_id ");
            	if (login.validate() && study_id > 0 && task_id > 0) {
        			// try and clear notification even if this wasn't started from a notification
        			TaskBroadcast.clearNotification(task_id);
        			Intent intent = new Intent("com.google.android.drdat.INSTRUCTIONS");
        			intent.putExtras(me.getIntent());            			
	            	me.startActivity(intent);
	            	me.finish();
            	} else {
            		new AlertDialog.Builder(me)
		            .setTitle("DRDAT ERROR")
		            .setMessage("No active participant "+Login.getLastEmail()+" with that password found!")
		            .setNeutralButton("Ok", new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
		            })
		            .create()
		            .show();
            	}
            }
        });

	}

}
