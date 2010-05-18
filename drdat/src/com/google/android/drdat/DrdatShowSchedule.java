package com.google.android.drdat;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Makes a dynamic list view of the schedule for a given participant.
 * The participant can click on any of the listed tasks to start them.
 * 
 * @author cal
 *
 */
public class DrdatShowSchedule extends Activity {
	private Login login;
	private ArrayList<Task> tasks;
	private ArrayList<String> entries;
	private final String LOG_TAG = "DRDAT SHOW SCHEDULE";
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    this.setContentView(R.layout.schedule);
	    
	    TextView tv = (TextView) findViewById(R.id.DrdatCLScheduleTitle);
	    
	    login = new Login(this);
	    tv.setText("Tasks for: "+login.getEmail()+"\nClick on a task to start it...");
	    
        DrdatSmi2TaskList tl = new DrdatSmi2TaskList(this, login.getEmail(), login.getPasswordMD5());
        Cursor tc = tl.getTaskListCursor();
        
        tasks = new ArrayList<Task>();
        entries = new ArrayList<String>();
        while (tc.moveToNext()) {
        	Task task = new Task(tc);
        	tasks.add(task);
        	entries.add(task.toString());
        }
        tc.close();
        
        ListView lv = (ListView) findViewById(R.id.DrdatCLScheduleList);
        
        lv.setAdapter(new ArrayAdapter<Task>(this, R.layout.schedule, R.id.DrdatCLScheduleListItems, tasks));
        
        lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int position, long id) {
				Intent i = new Intent();
				i.setClassName("com.google.android.drdat", "com.google.android.drdat.DrdatForms");
				i.putExtra("email", login.getEmail());
				i.putExtra("password", login.getPassword());
				i.putExtra("study_id", tasks.get(position).study_id);
				i.putExtra("task_id", tasks.get(position).task_id);
				startActivity(i);
			}
        });
        Log.d(LOG_TAG,"set click listener");
	}

}
