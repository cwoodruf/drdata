/**
 * 
 */
package com.google.android.drdat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * this takes care of uploading data from the phone to smi
 * @author cal
 *
 */
public class DrdatData2Smi extends Service {
	private Context me;
	private String LOG_TAG = "DRDAT DATA TO SMI";
	private UploadStatus status = UploadStatus.NOTRUN;
	private Thread uploading;
	
	private enum UploadStatus {
		NOTRUN, RUNNING, COMPLETE, ERROR
	}

	/**
	 * create a thread and try and send all unsent data to the smi
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		me = this;
		status = UploadStatus.NOTRUN;
		
		uploading = new Thread(new UploadThread());
		uploading.start();
		Log.d(LOG_TAG,"DrdatData2Smi.onCreate: started uploading (status "+status+") thread "+uploading);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	public static int clearUploaded(Context context) {
		ContentResolver cr = context.getContentResolver();
		Uri duri = Uri.parse(context.getString(R.string.DataUrl));
		return cr.delete(duri, "sent is not null", null);
	}
	
	private class UploadThread implements Runnable {
		@Override
		/**
		 * do the upload and then do a toast notification 
		 * to the end user that you have uploaded something
		 */
		public void run() {
			int uploaded = doUpload();
			if (uploaded > 0) {
				Looper.prepare();
			    Toast.makeText(
			    		me.getApplicationContext(),
			    		"Uploaded "+uploaded+" record"+(uploaded==1?"":"s"),
			    		Toast.LENGTH_LONG
			    	).show();
			    Looper.loop();
			    Looper.myLooper().quit();
			}
			stopSelf();
		}

		/**
		 * Do the bulk of the work for uploading data from the 
		 * drdat_data db managed by DrdatGUI's DrdatFormCollector. 
		 * Uses the content provider DrdatDataProvider to get the data
		 * and flag records that have been uploaded.
		 * 
		 * @return number of records uploaded
		 */
		private int doUpload() {
			int uploaded = 0;
			status = UploadStatus.RUNNING;
			ContentResolver cr = me.getContentResolver();
			Uri duri = Uri.parse(me.getString(R.string.DataUrl));
			Log.d(LOG_TAG,"content resolver "+cr);
			Cursor c = cr.query(
					duri,
					new String[] {
							"study_id",
							"task_id", 
							"email",
							"password",
							"query",
							"ts", 
							"sent"
					},
					"sent is null", 
					null, 
					null
				);
			if (c != null && c.moveToFirst()) {
				Log.d(LOG_TAG,"valid query "+c);
				do {
					try {
						String ts = c.getString(c.getColumnIndex("ts"));
						Log.d(LOG_TAG,"uploading data for "+ts);
						String email = c.getString(c.getColumnIndex("email"));
						String passwordMD5 = c.getString(c.getColumnIndex("password"));
						int study_id = c.getInt(c.getColumnIndex("study_id"));
						int task_id = c.getInt(c.getColumnIndex("task_id"));
						String query = c.getString(c.getColumnIndex("query"));

						String qs = 
							"do=sendData&"+
							"email="+Uri.encode(email)+"&"+
							"password="+Uri.encode(passwordMD5)+"&"+
							"ts="+Uri.encode(ts)+"&"+
							"study_id="+Uri.encode(Integer.toString(study_id))+"&"+
							"task_id="+Uri.encode(Integer.toString(task_id))+"&"+
							query;
						
						// Send data (from http://www.exampledepot.com/egs/java.net/post.html) 
						URL url = Smi.URL(me, "phone.php"); 
						URLConnection conn = url.openConnection(); 
						conn.setDoOutput(true); 
						OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream()); 
						wr.write(qs); 
						wr.flush(); 
						Log.d(LOG_TAG,"sent "+url.toExternalForm()+"?"+qs);
						
						// Get the response 
						BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream())); 
						String line = rd.readLine();
						
						// we've saved the data so we can indicate that in the db
						if (line != null && line.matches("OK \\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
							String sent = line.substring(3);
							ContentValues values = new ContentValues();
							values.put("sent", sent);
							String where = "study_id=? and task_id=? and email=? and password=? and ts=?";
							String[] selectionArgs = new String[] {
									Integer.toString(study_id),
									Integer.toString(task_id),
									email,
									passwordMD5,
									ts
							};
							uploaded += cr.update(duri, values, where, selectionArgs);
						}
						wr.close(); 
						rd.close();
						
					} catch (Exception e) {
						Log.e(LOG_TAG,"uploading error: "+e+": "+e.getMessage());
						status = UploadStatus.ERROR;
					}
				} while (c.moveToNext());
				Log.d(LOG_TAG,"finished cursor");
				
				if (status == UploadStatus.RUNNING) status = UploadStatus.COMPLETE;
				Log.i(LOG_TAG,"upload status "+status+" on "+(new Date()));
				
			}
			if (c != null) c.close();
			return uploaded;
		}
	}	
}
