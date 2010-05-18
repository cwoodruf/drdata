package com.google.android.drdat;

import java.util.TreeMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.webkit.WebView;

/**
 * Acts as an interface to the web form data. Takes care of saving web form data to
 * the drdat_data db. Form data is encoded via javascript and saved as a string in the
 * query field in the drdat_data table. This way we don't have to know what the structure
 * of the form is. Data is basically returned to the smi in this form with some additional
 * fields. DrdatDataProvider acts as a content provider for this class.
 * 
 * @author cal
 *
 */
public class DrdatFormCollector {
	private TreeMap<String,String> queryMap;
	private String query = "";
	private String action;
	private static String TAG = "DRDAT FORM";
	private final String LOG_TAG = DrdatFormCollector.TAG;
	private WebView mWebView;
	private Context context;
	private DrdatFormCache forms;
	private static final String DB_NAME = "drdat_data";
	private static final String DB_TABLE = "drdat_data";
	private static final int DB_VERSION = 1;
	private static final String DB_CREATE = 
			"create table " + DB_TABLE
			+ "(study_id integer, task_id integer, "  
			+ "email varchar(64), password varchar(64), "
			+ "query text, ts datetime default current_timestamp, sent datetime)";
	private DBHelper dbh;
	private SQLiteDatabase db;

	/**
	 * create a form collector from an activity
	 * @param c activity to act as context
	 */
	DrdatFormCollector(Activity c) {
		context = c;
    	startDB(c);
	}
	
	/**
	 * create a form collector from a context other than an activity
	 * @param c context
	 */
	DrdatFormCollector(Context c) {
		context = c;
    	startDB(c);
	}
	
	/**
	 * create a form collector from an activity, DrdatFormCache object and WebView object
	 * @param c context
	 * @param fc form cache
	 * @param wv web view object
	 */
    DrdatFormCollector(Activity c, DrdatFormCache fc, WebView wv) {
    	startDB(c);
    	queryMap = new TreeMap<String,String>();
    	context = c;
    	forms = fc;
    	mWebView = wv;
    }
    
    private void startDB(Context context) {
    	finalize();
    	dbh = new DBHelper(context);
    	db = dbh.getWritableDatabase();
    }
    
    protected void finalize() {
    	try {
	    	if (db != null) db.close();
	    	if (dbh != null) dbh.close();
    	} catch (Exception e) {
    		// ignore
    	}
    }
    
    /**
     * This func is mapped to the onload event for the page and fills the form with
     * any data we currently know about.
     * 
     * @param name of field to get from our queryMap
     * @return field data stored in queryMap
     */
    public String getField(String name) {
    	try {
    		if (queryMap.containsKey(name)) {
    			return queryMap.get(name);
    		}
    	} catch (Exception e) {
    		Log.e(LOG_TAG,"getField error for "+name+". Exception "+e.toString()+": "+e.getMessage());
    	}
		return "";
    }
    
    /**
     * have we every seen this field before?
     * @param name of the field
     * @return true if we've seen this field before
     */
    public boolean exists(String name) {
    	try {
    		return queryMap.containsKey(name);
		} catch (Exception e) {
			Log.e(LOG_TAG,"DrdatFormCollector exists: error for "+name+": "+e+": "+e.getMessage());
		}
		return false;
    }
    /**
     * This func is mapped to the onSubmit event and saves any new data for this form
     * to our queryMap abstraction of the cgi query.
     * 
     * @param name of field
     * @param value entered or selected
     */
    public void setField(String name, String value) {
		if (name.equals("action")) {
			setAction(value);
			return;
		}
		if (name.equals("")) {
			return;
		}
		queryMap.put(name, value);
		Log.i(LOG_TAG, "setField: name " + name + " value " + value);
    }

    /**
     * Responds to an action on the form.
     * You can move forward, go back or save the data.
     * 
     * This is currently decoupled from actually sending the data.
     *  
     * @param data
     */
    public void doAction(String action) {
    	try {
    		this.action = action;
	    	if (getAction().matches(".*next.*")) {
	    		forms.nextForm();
			} else if (getAction().matches(".*prev.*")) {
	    		forms.prevForm();
	    	} else if (getAction().matches(".*save.*data.*")) {
	            new AlertDialog.Builder(context)
		            .setTitle("DRDAT")
		            .setMessage("Save Entered Data?")
		            .setNegativeButton("Don't Save", new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							((Activity) context).finish();							
						}
		            })
		            .setNeutralButton("Cancel", new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
		            })
		            .setPositiveButton("Save", new OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								if (saveQueryToDB()) {
									
									Intent service = 
										new Intent("com.google.android.drdat.UPLOAD_DATA");
									context.stopService(service);
									context.startService(service);
									
						            new AlertDialog.Builder(context)
							            .setTitle("DRDAT")
							            .setMessage("Data saved. Thank you.")
							            .setNeutralButton("Ok", new OnClickListener() {
											public void onClick(DialogInterface dialog, int which) {
												((Activity) context).finish();
											}
							            })
							            .create()
							            .show();
									
								} else {
						            new AlertDialog.Builder(context)
							            .setTitle("DRDAT ERROR")
							            .setMessage("Error saving data! Please try again.")
							            .setNeutralButton("Ok", new OnClickListener() {
											public void onClick(DialogInterface dialog, int which) {
												dialog.cancel();
											}
							            })
							            .create()
							            .show();
								}
							}
		            })
		            .create()
		            .show();
	    	}
	    	
	    	Log.d(LOG_TAG,"about to run loadData for form "+forms.getCurrForm()+" action "+getAction());
			mWebView.loadData(forms.generate(), forms.getMime(), forms.getEncoding());
	    } catch (Exception e) {
    		Log.e(LOG_TAG,"doAction display next form error: "+e.toString()+": "+e.getMessage());
    	}
    }
    /**
     * Saves the data we've accumulated. 
     * The cl will periodically gather this data and 
     * send it to smi via a content provider interface.
     * 
     * @return true if we were able to save the data to the db
     */
    public boolean saveQueryToDB() {
    	try {
			query = "";
			for (Object name: queryMap.keySet()) {
				query += name + "=" + queryMap.get(name) + "&";
			}
			Log.i(LOG_TAG,"saving query "+query);
    	} catch (Exception e) {
    		Log.e(LOG_TAG,"error generating query "+query);
    		Log.e(LOG_TAG,"exception "+e.toString()+": "+e.getMessage());
    	}
    	try {
			ContentValues values = new ContentValues();
			values.put("query", query);
			values.put("study_id", forms.getStudy_id());
			values.put("task_id", forms.getTask_id());
			
			values.put("email", Login.getLastEmail());
			values.put("password", Login.getLastPasswordMD5());
			
			db.insert(DB_NAME, null, values);
			return true;
			
    	} catch (Exception e) {
    		Log.e(LOG_TAG,"saveQueryToDB: db error: "+e.toString()+": "+e.getMessage());
    	}
    	return false;
    }
    
    /**
     * Query method used by the DrdatDataProvider content provider. 
     * Uses the context provided to make a new DrdatFormCollector object and run the query.
     * 
     * @param context what called us
     * @param projection fields to get
     * @param selection where clause minus "where"
     * @param selectionArgs data for any place holder "?"s in the selection
     * @param sortOrder order by clause w/o the "order by"
     * 
     * @return cursor to query results
     */
    public static Cursor query(
    		Context context,
    		String[] projection, 
    		String selection, 
    		String[] selectionArgs, 
    		String sortOrder) 
    {
    	DrdatFormCollector data = new DrdatFormCollector(context);
    	return data.query(projection, selection, selectionArgs, sortOrder);
    }
    	
    /**
     * See static method: does the actual query.
     * 
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * 
     * @return cursor
     */
    public Cursor query(
    		String[] projection, 
    		String selection, 
    		String[] selectionArgs, 
    		String sortOrder) 
    {
    	Cursor c = null;
		try {
			c = db.query(DrdatFormCollector.DB_TABLE,projection,selection,selectionArgs,null,null,sortOrder);
			
		} catch (Exception e) {
			Log.e(TAG,"DrdatFormCollector.clearSent: "+e+": "+e.getMessage());
		}
    	return c;
    }
    
    /**
     * Static method that uses a provided context to make a DrdatFormCollector object and
     * run the non-static update method on it.
     * 
     * @param context who called us
     * @param values ContentValues object with data we want to update
     * @param whereClause where clause w/o the "where"
     * @param whereArgs string array of arguments for place holder "?"s in the where clause
     * 
     * @return number of records changed
     */
    public static int update(
    		Context context,
    		ContentValues values,
    		String whereClause,
    		String[] whereArgs) 
    {
    	DrdatFormCollector data = new DrdatFormCollector(context);
    	return data.update(values, whereClause, whereArgs);
    }
    
    /**
     * See static version above. Does the actual update query.
     * 
     * @param values
     * @param whereClause
     * @param whereArgs
     * 
     * @return records updated
     */
    public int update(
    		ContentValues values,
    		String whereClause,
    		String[] whereArgs) 
    {
    	int changed = -1;
    	try {
    		changed = db.update(DrdatFormCollector.DB_TABLE, values, whereClause, whereArgs);
    		
    	} catch (Exception e) {
    		Log.e(DrdatFormCollector.TAG,"DrdatFormCollector.update: "+e+": "+e.getMessage());
    	}
    	return changed;
    }

    /**
     * Static method that will delete a selection of records. Note that this cannot be undone.
     * 
     * @param context who called us
     * @param selection where part of sql statement w/o the "where"
     * @param selectionArgs any arguments needed to replace "?" place holders in selection
     * 
     * @return number of records deleted
     */
    public static int delete(Context context, String selection, String[] selectionArgs) {
    	DrdatFormCollector data = new DrdatFormCollector(context);
    	return data.delete(selection, selectionArgs);    	
    }
    
    /**
     * Actually does the work of the static method above.
     * 
     * @param whereClause
     * @param whereArgs
     * 
     * @return records deleted
     */
    public int delete(String whereClause, String[] whereArgs) {
    	int changed = -1;
    	try {
    		changed = db.delete(DrdatFormCollector.DB_TABLE, whereClause, whereArgs);
    		
    	} catch (Exception e) {
    		Log.e(DrdatFormCollector.TAG,"DrdatFormCollector.delete: "+e+": "+e.getMessage());
    	}
    	return changed;
    }
    
    /**
     * Private class that provides an interface for working with SQLiteDatabase objects
     * 
     * @author cal
     *
     */
    private static class DBHelper extends SQLiteOpenHelper {

		DBHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DB_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}

    // automatically generated getters and setters
	public void setQueryMap(TreeMap<String,String> q) {
		this.queryMap = q;
	}
	public TreeMap<String,String> getQueryMap() {
		return queryMap;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String q) {
		query = q;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getAction() {
		return action;
	}
}
