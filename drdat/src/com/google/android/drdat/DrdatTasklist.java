/**
 * 
 */
package com.google.android.drdat;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * Content provider that wraps DrdatSmi2TaskList so 
 * DrdatGUI can get tasks and schedules.
 * Only the query method is implemented as the data is read only.
 * 
 * @author cal
 *
 */
public class DrdatTasklist extends ContentProvider {
	private String email;
	private String passwordMD5;
	public final String TYPE = "DrdatTasklist";
	private final String LOG_TAG = "DRDAT TASKLIST PROVIDER";
	
	/* (non-Javadoc)
	 * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// read only
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri uri) {
		return TYPE;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// read only
		return null;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		// read only so nothing really to do
		return true;
	}
	
	/**
	 * Gets a list of tasks for a participant with schedule data. This uses
	 * DrdatSmi2TaskList to manage task / schedule data. If the projection requests
	 * a "tasklist" the drdat_tasks table is queried and if not then drdat_studies is
	 * queried instead.
	 * 
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)

 	 * @param uri - uri we were called with
	 * @param projection - if the first element is "tasklist" return a list of tasks otherwise return study info
	 * @param selection - sql where clause (not used)
	 * @param selectionArgs - contains the email, passwordMD5 we want
	 * @param sortOrder - order by clause (not used)
	 * 
	 * @return cursor with the task / schedule data for this participant

	 */
	@Override
	public Cursor query(
			Uri uri, 
			String[] projection, 
			String selection,
			String[] selectionArgs, 
			String sortOrder
		) 
	{
		email = selectionArgs[0];
		passwordMD5 = selectionArgs[1];
		// make reloads happen explicitly instead of implicitly - may want to reinstate this at some point however
		boolean dbfound = false;
		for (String db: getContext().databaseList()) {
			Log.d(LOG_TAG,"checking "+db);
			if (db.equals(DrdatSmi2TaskList.DB_NAME)) {
				dbfound = true;
				break;
			}
		}
		DrdatSmi2TaskList tl = new DrdatSmi2TaskList(getContext(),email,passwordMD5);
		if (!dbfound || DrdatLogin.newLogin(email,passwordMD5) ) {
			Log.d(LOG_TAG, DrdatSmi2TaskList.DB_NAME+" not found: rebuilding");
			tl.reload();
		}
		if (projection[0] == "tasklist") {
			return tl.getTaskListCursor();
		} else {
			return tl.getStudyCursor();
		}
	}
	
	/* (non-Javadoc)
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// read only
		return 0;
	}

}
