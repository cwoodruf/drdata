package com.google.android.drdat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Main entry point for manually managing tasks, schedules and data uploads
 * Uses a ListView with entries in strings.xml.
 * If the notification alarm hasn't been initiated or turned off DrdatCommunications
 * will try and start it automatically when it is made visible.
 * 
 * @author cal
 *
 */
public class DrdatCommunications extends Activity {
	private Activity me;
	private TextView t;
	private String LOG_TAG = "DRDAT COMMS";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        me = this;

        // try and set the notification alarm
		// tell user alarm status
		t = (TextView) findViewById(R.id.DrdatCLActionsTitle);
		if (AlarmRefresh.wasStopped() == false && AlarmRefresh.getDailyCron() == null) {
			AlarmRefresh.setAlarm(me,AlarmRefresh.SIXTYSECS);			
			if (AlarmRefresh.getDailyCron() == null) {
				Toast.makeText(me, R.string.NoAlarms, Toast.LENGTH_LONG).show();
				t.setText(me.getString(R.string.DrdatCLActionsTitle)+"\n"+me.getString(R.string.AlarmFailed));
			} else {
				Toast.makeText(me, R.string.AlarmStart, Toast.LENGTH_LONG).show();
				t.setText(me.getString(R.string.DrdatCLActionsTitle)+"\n"+me.getString(R.string.AlarmStart));
			}
		} else {
			if (AlarmRefresh.wasStopped()) {
				t.setText(me.getString(R.string.DrdatCLActionsTitle)+"\n"+me.getString(R.string.NoAlarms));
			} else if (AlarmRefresh.getDailyCron() != null){
				t.setText(me.getString(R.string.DrdatCLActionsTitle)+"\n"+me.getString(R.string.AlarmStart));
			}
		}
		
        ListView l = (ListView) findViewById(R.id.list);
        l.setTextFilterEnabled(true);
        
        l.setOnItemClickListener(new OnItemClickListener() {
        	@Override
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		// When clicked, show a toast with the TextView text
        		String clicked = ((TextView) view).getText().toString();
        		if (clicked == getString(R.string.Help)) {
        			Intent i = new Intent("com.google.android.drdat.HELP");
        			me.startActivity(i);
        			
        		} else if (clicked == getString(R.string.UpdateSchedule)) {
        			Intent i = new Intent("com.google.android.drdat.UPDATE_SCHEDULE");
        			startActivity(i);
        			
        		} else if (clicked == getString(R.string.ShowSchedule)) {
        			Intent i = new Intent("com.google.android.drdat.SHOW_SCHEDULE");
        			startActivity(i);
            			
        		} else if (clicked == getString(R.string.DeleteTasks)) {
        			new AlertDialog.Builder(me)
    				.setTitle(me.getString(R.string.DeleteTasks))
    				.setMessage(R.string.ReallyDelete)
    				.setPositiveButton("Delete", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							// WARNING: this will permanently delete all task and login data!!!
							StringBuffer dbs = new StringBuffer();
							try {
								for (String db :me.databaseList()) {
									if (!db.matches("drdat.*")) continue;
									Log.d(LOG_TAG,"deleting db "+db);
									me.deleteDatabase(db);
									dbs.append(db);
								}
								
							} catch (Exception e) {
								Log.e(
										LOG_TAG,
										"DrdatCommunications "+getString(R.string.DeleteTasks)+
										": "+e+": "+e.getMessage()
									);
							}
	            			
							Toast.makeText(me, R.string.TasksDeleted, Toast.LENGTH_LONG).show();
	            			Log.i(LOG_TAG,"deleted all task and login data");
	            			dialog.cancel();
						}
    				})
    				.setNegativeButton("Cancel", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
    				})
    				.show();
        			
        		} else if (clicked == getString(R.string.Notify)) {
        			// turn eternal alarms on the task notification alarms are for specific times of day
        			// so we have to reload them periodically 
        			AlarmRefresh.setAlarm(me,AlarmRefresh.SIXTYSECS);
        			Toast.makeText(me, R.string.AlarmStart, Toast.LENGTH_LONG).show();
        			t.setText(me.getString(R.string.DrdatCLActionsTitle)+"\n"+me.getString(R.string.AlarmStart));
        			    				
        		} else if (clicked == getString(R.string.NotifyStop)) {
        			if (AlarmRefresh.getDailyCron() != null) {
        				AlarmRefresh.clearAlarm();
            			Toast.makeText(me, R.string.AlarmStop, Toast.LENGTH_LONG).show();
        				t.setText(me.getString(R.string.DrdatCLActionsTitle)+"\n"+me.getString(R.string.AlarmStop));
            		} else {
            			Toast.makeText(me, R.string.NoAlarms, Toast.LENGTH_LONG).show();
        			}
        			
        		} else if (clicked == getString(R.string.UploadData)) {
        			Intent service = new Intent(me,com.google.android.drdat.DrdatData2Smi.class);
        			me.stopService(service);
        			ComponentName running = me.startService(service);
        			Toast.makeText(me, R.string.StartedUpload, Toast.LENGTH_LONG).show();
        			Log.d(LOG_TAG,"running upload: "+running);
        			
        		} else if (clicked == getString(R.string.ClearSentData)) {
        			new AlertDialog.Builder(me)
        				.setTitle(me.getString(R.string.ClearSentData))
        				.setMessage(R.string.ReallyDelete)
        				.setPositiveButton("Delete", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
		        				int deleted = DrdatData2Smi.clearUploaded(me);
		            			Toast.makeText(me, me.getString(R.string.NumDeleted)+" = "+deleted, Toast.LENGTH_LONG).show();
		            			Log.i(LOG_TAG,"deleted "+deleted+" uploaded data records");
		            			dialog.cancel();
							}
        				})
        				.setNegativeButton("Cancel", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
        				})
        				.show();
        			
        		} else if (clicked == getString(R.string.Settings)) {
        			Intent i = new Intent("com.google.android.drdat.SETTINGS");
        			me.startActivity(i);
        			
        		}
        	}
        });
    }
}