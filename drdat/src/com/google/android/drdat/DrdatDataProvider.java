/**
 * 
 */
package com.google.android.drdat;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Content provider that gives access to participant generated data.
 * Used to get data, update the sent field and delete sent data. DrdatCL,
 * the communications layer, takes care of sending data to smi. It uses
 * this provider to manage the data for the upload process.
 * 
 *  The delete, update and query methods are implemented. DrdatFormCollector
 *  actually does the work. These are designed to be more open ended in their
 *  operation. 
 *  
 * @author cal
 *
 */
public class DrdatDataProvider extends ContentProvider {

	/**
	 * Delete a selection of data records. Generally for deleting items already sent.
	 * 
	 * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) { 
		return DrdatFormCollector.delete(getContext(),selection,selectionArgs); 
	}
	
	/* (non-Javadoc)
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri uri) {
		return "DrdatDataProvider";
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// we don't add records externally
		return null;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		// we don't create a db here 
		return false;
	}

	/**
	 * Get a selection of drdat_data records. Generally used to get records where the sent field is null.
	 * 
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	public Cursor query(
			Uri uri, 
			String[] projection, 
			String selection,
			String[] selectionArgs, 
			String sortOrder) 
	{
		return DrdatFormCollector.query(getContext(),projection,selection,selectionArgs,sortOrder);
	}
	
	/**
	 * Update drdat_data records. Generally used to set the sent field to the timestamp sent back from the smi 
	 * when an upload was successful. Data records are identified by email, password MD5 hash, study_id, task_id,
	 * and timestamp.
	 * 
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(
			Uri uri, 
			ContentValues values, 
			String selection,
			String[] selectionArgs) 
	{
		return DrdatFormCollector.update(getContext(),values,selection,selectionArgs);
	}
}
