package com.google.android.drdat;

import java.util.ArrayList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * This broadcast receiver turns on the periodic task that checks to see if we need 
 * bug the participant to do something.
 * To do something it sends a broadcast which can then be picked up by DrdatGUI's
 * TaskBroadcast to make a notification.
 * 
 * @author cal
 *
 */
public class AlarmRefresh extends BroadcastReceiver {
	private static String TAG = "DRDAT ALARM REFRESH";
	private final String LOG_TAG = AlarmRefresh.TAG;
	private static AlarmManager dailyCron;
	private static PendingIntent dailyOp;
	private static AlarmState state = AlarmState.UNSET;
	public static long SIXTYSECS = 60;
	// reduced for testing purposes
	public static long ONEDAY = 86400000; 
	public static long ONEHOUR = 3600000;
	public static long ONEMINUTE = 60000;
	
	private static enum AlarmState {
		UNSET, STARTED, STOPPED
	}
	@Override
	/**
	 * use DrdatSmi2TaskList to figure out if we need to bug the participant
	 * send broadcast to DrdatGUI's TaskBroadcast if we do
	 * 
	 * @param context - our context (mainly needed for db access)
	 * @param intent - intent we were called with (not used)
	 */
	public void onReceive(Context context, Intent intent) {
		// check intents every minute
		ArrayList<Intent> intents = DrdatSmi2TaskList.getCurrentAlarms(context);
		if (intents != null) {
			for (Intent i: intents) {
				Log.d(LOG_TAG,"broadcast intent "+i);
				context.sendBroadcast(i);
			}
		}
		// do automatic data uploads and refresh user task data less frequently
		SharedPreferences prefs = context.getSharedPreferences("AlarmRefresh", Context.MODE_PRIVATE);
		long lastDataUpload = prefs.getLong("lastDataUpload", 0);
		long lastTaskRefresh = prefs.getLong("lastTaskRefresh", 0);
		Editor ed = prefs.edit();
		long now = System.currentTimeMillis(); 
		if (now - lastDataUpload > AlarmRefresh.ONEHOUR) {
			Intent service = new Intent(context,DrdatData2Smi.class);
			context.stopService(service);
			context.startService(service);
			ed.putLong("lastDataUpload", now);
		}
		if (now - lastTaskRefresh > AlarmRefresh.ONEDAY) {
			DrdatSmi2TaskList.refreshEverything(context);
			ed.putLong("lastTaskRefresh", now);
		}
		ed.commit();
	}
	
	/**
	 * gets the alarm service and sets it to a repeating alarm that pings itself (see onReceive) periodically
	 * 
	 * @param me - our context
	 * @param checkevery - how often to check in seconds
	 */
	public static void setAlarm(Context me, long checkevery) {
		Intent i = new Intent("com.google.android.drdat.ALARM_REFRESH");
		AlarmRefresh.dailyOp = PendingIntent.getBroadcast(me, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmRefresh.dailyCron = (AlarmManager) me.getSystemService(Context.ALARM_SERVICE);
		AlarmRefresh.dailyCron.setRepeating(
				AlarmManager.RTC_WAKEUP, // how to interpret next arguments 
				System.currentTimeMillis(), // start right away
				(checkevery * 1000), 
				AlarmRefresh.dailyOp // what to do
			);
		state = AlarmState.STARTED;
	}

	public static void clearAlarm() {
		if (AlarmRefresh.dailyCron != null) {
			AlarmRefresh.dailyCron.cancel(AlarmRefresh.dailyOp);
		}
		state = AlarmState.STOPPED;
	}
	
	public static PendingIntent getDailyOp() {
		return dailyOp;
	}

	public static AlarmManager getDailyCron() {
		return dailyCron;
	}
	
	/**
	 * check whether the alarm was deliberately stopped
	 * we can use this to decide whether to leave the alarm off
	 * when the task manager gets started: 
	 * by default it will try and start notifications
	 * @return true if we deliberately stopped the alarm false otherwise
	 */
	public static boolean wasStopped() {
		boolean stopped = (state == AlarmState.STOPPED ? true : false);
		Log.d(TAG, "alarm state = "+state+", alarm deliberately stopped = "+stopped);
		return stopped;
	}
}
