/**
 * 
 */
package com.google.android.drdat;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Content provider that returns the forms for a specific task.
 * The query(...) method is the only method that really does anything 
 * in this interface.
 * 
 * @author cal
 *
 */
public class DrdatTask extends ContentProvider {
	public final String TYPE = "DrdatTask";
	private int task_id;
	private int study_id;
	private String email;
	private String passwordMD5;
	
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
		// read only: nothing to do on create
		return true;
	}

	/**
	 * Grabs the study and task information for a given participant / task.
	 * Needs the email, passwordMD5, study_id and task_id in the selectionArgs 
	 * in order to find or generate the form data for that task.
	 * 
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 * @param uri - uri we were called with
	 * @param projection - fields we want (not used)
	 * @param selection - sql where clause (not used)
	 * @param selectionArgs - contains the study_id, task_id, email, passwordMD5 we want
	 * @param sortOrder - order by clause (not used)
	 * 
	 * @return cursor with the form data for this task
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
		DrdatSmi2Task forms = new DrdatSmi2Task(getContext());
		study_id = Integer.parseInt(selectionArgs[0]);
		task_id = Integer.parseInt(selectionArgs[1]);
		email = selectionArgs[2];
		passwordMD5 = selectionArgs[3];
		return forms.getRawForms(study_id, task_id, email, passwordMD5);
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		// read only
		return 0;
	}

}
