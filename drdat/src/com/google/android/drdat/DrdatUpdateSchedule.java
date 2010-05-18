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
import android.widget.Toast;

/**
 * Presents a login form so that a participant can make a web
 * query to the smi to update their task list and reminder schedule. 
 * This uses DrdatSmi2TaskList to update the schedule for tasks. 
 * This will also grab form data for each task for that user.
 * 
 * @author cal
 *
 */
public class DrdatUpdateSchedule extends Activity {
	private final String LOG_TAG = "DRDAT UPDATE SCHED";
	private EditText passwordView;
	private EditText emailView;
	private Activity me;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.update);
        
	    me = this;
	    Login cache = new Login(me);
        emailView = (EditText) findViewById(R.id.UpdateLoginEmail);
        emailView.setText(cache.getEmail());
        
        passwordView = (EditText) findViewById(R.id.UpdateLoginPassword);
        passwordView.setText(cache.getPassword());
        
        Button login = (Button) findViewById(R.id.UpdateLoginButton);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	String email = emailView.getText().toString();
            	String pw = passwordView.getText().toString();

            	Login login = new Login(me,email,pw);
                if (login.validate()) {
                	login.save();
                	String passwordMD5 = login.getPasswordMD5();
                
                	DrdatSmi2TaskList tasks = new DrdatSmi2TaskList(me,email,passwordMD5);
                    tasks.reload();
                    
                    if (tasks.isHttpFailed()) {
            			Toast.makeText(me, R.string.UpdateFailed, Toast.LENGTH_LONG).show();
                    
                    } else {
                    	Log.d(LOG_TAG,"attempting to update forms.");
                    	DrdatSmi2Task forms = new DrdatSmi2Task(me);
                    	forms.deleteForms(email, passwordMD5);
                    	for (Task task: tasks.getTasks()) {
                    		Log.d(LOG_TAG,"inserting data for "+task.task_id+" "+email+" "+passwordMD5);
                    		forms.insertTask(task.study_id, task.task_id, email, passwordMD5);
                    	}
                    
                    }
                    
                    Intent i = new Intent("com.google.android.drdat.SHOW_SCHEDULE");
                    me.startActivity(i);
                    me.finish();
                
                } else {
                	new AlertDialog.Builder(me)
			            .setTitle("DRDAT ERROR")
			            .setMessage("Participant "+email+" is not active in any study with that password!")
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
