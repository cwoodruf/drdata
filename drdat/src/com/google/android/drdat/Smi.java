package com.google.android.drdat;

import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Provide access to the SMI url. The base url is defined in strings.xml as SmiUrl.
 * The url can be changed to another value. Any new url is stored in shared preferences.
 * 
 * @author cal
 *
 */
public class Smi {
	private static String SMI_PREFS = "smi";
	private static String SMI_URL = "SmiUrl";

	/**
	 * Gets the current smi url 
	 * @param c context to use to find url setting
	 * @return url string
	 */
	public static String str(Context c) {
		return str(c, "");
	}
	
	/**
	 * Gets the smi url and appends a user defined string to it.
	 * @param c context to use to find url
	 * @param extras additional url string (eg file path and query string)
	 * @return the concatenated url
	 */
	public static String str(Context c, String extras) {
		return get(c) + extras;
	}
	
	/**
	 * Gets the smi url as a URL object.
	 * @param c context to use to get smi url
	 * @return URL object based on smi url
	 */
	public static URL URL(Context c) {
		return URL(c, "");
	}
	
	/**
	 * Gets the smi url as a URL object will append 
	 * an extra string to the smi url before making the URL object.
	 * @param c context to use to get smi url
	 * @param extras extra string to add to url
	 * @return URL object of full url
	 */
	public static URL URL(Context c, String extras) {
		try {
			return new URL(str(c, extras));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * internal method to get the base smi url
	 * @param context to use to get smi url
	 * @return smi url string
	 */
	private static String get(Context context) {
		SharedPreferences up = context.getSharedPreferences(SMI_PREFS, Context.MODE_PRIVATE);
		return up.getString(SMI_URL, context.getString(R.string.SmiUrl));
	}
	
	/**
	 * Change the smi url to another url. You need to use a fully qualified url such as:
	 * http://somedomain.org/someurl/
	 * The url should end in /.
	 * @param context to use for setting the smi url in shared preferences
	 * @param url to save
	 */
	public static void set(Context context, String url) {
		SharedPreferences up = context.getSharedPreferences(SMI_PREFS, Context.MODE_PRIVATE);
		Editor ed = up.edit();
		if (url.matches(".*/") == false) url += "/";
		ed.putString(SMI_URL, url);
		ed.commit();
	}
	
	/**
	 * Change the smi url back to the default defined in strings.xml.
	 * @param context to use to get shared preferences and strings.xml.
	 */
	public static void reset(Context context) {
		SharedPreferences up = context.getSharedPreferences(SMI_PREFS, Context.MODE_PRIVATE);
		Editor ed = up.edit();
		ed.putString(SMI_URL, context.getString(R.string.SmiUrl));
		ed.commit();
	}
	
	/**
	 * Get the default smi url as defined in strings.xml
	 * @param context to find strings.xml data
	 * @return default smi url
	 */
	public static String defUrl(Context context) {
		return context.getString(R.string.SmiUrl);
	}
}
