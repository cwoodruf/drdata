package com.google.android.drdat;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * What the drdat gui app uses to get cached login information.
 * This is a very simple content provider that only checks the
 * user name and password. It will cache the last login to 
 * allow us to automatically populate log in forms.  
 *  
 * @author cal
 *
 */
public class DrdatLogin extends ContentProvider {
	private Login cache;
	private final String LOG_TAG = "DRDAT LOGIN";
	public static HashMap<String,Boolean> exists;
	
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// we never delete logins rather we keep a record of all login attempts
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		return "DrdatLogin";
	}

	@Override
	/**
	 * Gets the entered email and password and saves it to drdat_login database 
	 * managed by DrdatLoginCache. Will not save an invalid email and password.
	 * 
	 * @param uri - how this was called (not used)
	 * @param values - values including email and password
	 */
	public Uri insert(Uri uri, ContentValues values) {
		String email = values.getAsString("email");
		String password = values.getAsString("password");
		cache = new Login(getContext(),email,password);
		try {
			if (!cache.validate()) return null;
			cache.save();
		} catch (Exception e) {
			Log.e(LOG_TAG, "DrdatLogin insert: "+e.toString()+": "+e.getMessage());
		}
		return uri;
	}

	@Override
	public boolean onCreate() {
		return false;
	}

	@Override
	/**
	 * Find the email and password entered and return the md5 hash of the password.
	 * 
	 * @param uri - uri we were called with
	 * @param projection - fields to get (not used)
	 * @param selection - how to find them (not used)
	 * @param selectionArgs - may contain a user name and password
	 * @param sortOrder - order by clause (not used)
	 * 
	 * @return cursor with at least the passwordMD5 field
	 */
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		cache = new Login(getContext());
		
		if (selectionArgs != null && selectionArgs.length > 0) {
			String email = selectionArgs[0];
			String passwordMD5 = selectionArgs[1];
			cache.setEmail(email);
			cache.setPassword(passwordMD5);
			
			// see if this is a new login and remember that
			if (exists == null) exists = new HashMap<String,Boolean>();
			Cursor indb = cache.getCursor();
			String key = makeKey(email,passwordMD5);
			if (indb == null || indb.getCount() == 0) {
				exists.put(key, false);
			} else {
				exists.put(key, true);
			}
			indb.close();
			
			if (!cache.validate()) return null; // done as a courtesy - however requires an extra web request
			// saving the cache logs this login event
			try {
				cache.save();
			} catch (Exception e) {
				Log.e(LOG_TAG, "DrdatLogin insert: "+e.toString()+": "+e.getMessage());
			}
			// setting the email and password has the side effect of 
			// making getCursor only look for that email and password
			return cache.getCursor();
		}
		// retrieve the last login w/o regard to who logged in
		return cache.retrieveLastLogin();
	}
	
	/**
	 * Tells us if we've seen this login before in this session. 
	 * If we have seen it this session check whether they had logged in before.
	 * @param email
	 * @param passwordMD5
	 * @return true if either we haven't seen this login before or they've never logged in before
	 */
	public static boolean newLogin(String email, String passwordMD5) {
		String key = makeKey(email,passwordMD5);
		if (exists == null) return true;
		if (!exists.containsKey(key)) return true;
		if (exists.get(key) == false) return true;
		return false;
	}
	
	private static String makeKey(String email, String passwordMD5) {
		return email+","+passwordMD5;
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// we always insert query only gets the last record
		return 0;
	}
}
