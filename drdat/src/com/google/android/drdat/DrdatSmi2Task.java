package com.google.android.drdat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.database.Cursor;
import android.database.sqlite.*;

/**
 * Use the smi to get the forms for a specific task. This class will save form
 * data to a local database drdat_forms which can then be used offline. In
 * general the phone system (DrdatCL and DrdatGUI) should work if the smi is
 * offline.
 * 
 * Forms are provided from the smi as html with &lt;!-- split --&gt; pseudo tags
 * that are used to indicate different forms. The raw form data is what is saved
 * in the db: we don't try and save the split up forms separately.
 * 
 * @author cal
 * 
 */
public class DrdatSmi2Task {

	private final String LOG_TAG = "DRDAT FORM PROVIDER";

	private Context context;

	private DBHelper dbh;
	private SQLiteDatabase db;

	private static final String DB_NAME = "drdat_forms";
	private static final String DB_TABLE = "drdat_forms";
	private static final int DB_VERSION = 3;
	private static final String DB_ROWID = "study_id=? and task_id=? and email=? and password=?";
	private static final String DB_CREATE = "create table "
			+ DB_TABLE
			+ "(study_id integer, task_id integer, email varchar(64), password varchar(32), forms text, "
			+ "constraint " + DB_TABLE
			+ "_pkey primary key (study_id, task_id, email, password))";

	public DrdatSmi2Task(Context ctx) {
		context = ctx;
		dbh = new DBHelper(context);
		db = dbh.getWritableDatabase();
	}

	protected void finalize() {
		try {
			db.close();
			dbh.close();
		} catch (Exception e) {
			// ignored
		}
	}

/**
	 * from the notepad tutorial - interface to our form db
	 * 
	 * {@linkplain http://developer.android.com/resources/tutorials/notepad/codelab/NotepadCodeLab.zip}
	 * {@linkplain http://developer.android.com/resources/tutorials/notepad/index.html
	 * 
	 * @author android 
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
			if (oldVersion != newVersion) {
				db.execSQL("drop table if exists " + DB_TABLE);
				db.execSQL(DB_CREATE);
			}
		}
	}

	/**
	 * container for our query data
	 * 
	 * @author cal
	 * 
	 */
	public class FormData {
		// raw form data
		public int study_id;
		public int task_id;
		public String email;
		public String passwordMD5;

		// containers for db funcs
		public String[] args;
		public String[] projection;

		public FormData(int study_id, int task_id, String email, String pw) {
			this.study_id = study_id;
			this.task_id = task_id;
			this.email = email;
			this.passwordMD5 = pw;

			args = new String[] { Integer.toString(study_id),
					Integer.toString(task_id), email, passwordMD5 };

			projection = new String[] { "forms" };

		}
	}

	/**
	 * Grabs the forms either from the local db or the smi fills the forms class
	 * member. Note that we use all of the study_id, task_id, email and password
	 * to identify the forms that belong to a specific task. The study_id is
	 * more for convenience later when we collate data.
	 * 
	 * @param study_id
	 * @param task_id
	 * @param email
	 * @param passwordMD5
	 *            - hash of password
	 */
	public Cursor getRawForms(int study_id, int task_id, String email,
			String passwordMD5) {
		Cursor c = null;
		try {
			FormData fd = new FormData(study_id, task_id, email, passwordMD5);
			String query = "select forms from " + DB_TABLE + " where "
					+ DB_ROWID;
			c = db.rawQuery(query, fd.args);
			Log.i(LOG_TAG, "query " + query + " args "
					+ String.format("[%s, %s, %s, %s]", ((Object[]) fd.args)));

			if (c == null || c.getCount() == 0) {
				boolean insert = false;
				if (c == null || c.getCount() == 0)
					insert = true;
				if (c != null)
					c.close();

				Log.i(LOG_TAG, "Inserting: email " + fd.email + " pw "
						+ fd.passwordMD5 + " study_id " + fd.study_id
						+ " task_id " + fd.task_id);
				refreshTask(insert, fd);
				return db.rawQuery(query, fd.args);

			} else {
				Log.i(LOG_TAG, "sqlite: found a row!");
			}

		} catch (Exception e) {
			Log.e(LOG_TAG, "DrdatSmi2Task getRawForms: " + e.toString() + ": "
					+ e.getMessage());
		}
		return c;
	}

	/**
	 * Delete all form data for a given user. Generally this is done when the
	 * task list is updated for that user. Then use insertTask to rebuild form
	 * data when refreshing.
	 */
	public void deleteForms(String email, String passwordMD5) {
		try {
			db.execSQL("delete from " + DB_TABLE
					+ " where email=? and password=?", new String[] { email,
					passwordMD5 });
		} catch (Exception e) {
			Log.e(LOG_TAG, "DrdatSmi2Task deleteForms: " + e.toString() + ": "
					+ e.getMessage());
		}
	}

	/**
	 * Delete everything: used for cleaning a phone up for another user
	 * 
	 * @see DrdatSmi2TaskList.deleteEverything()
	 */
	public static void deleteEverything(Context context) {
		DrdatSmi2Task t = new DrdatSmi2Task(context);
		try {
			t.db.execSQL("delete from " + DB_TABLE);

		} catch (Exception e) {
			Log.e(t.LOG_TAG, "DrdatSmi2Task.deleteEverything: " + e + ": "
					+ e.getMessage());
		}
	}

	/**
	 * Works in conjunction with deleteForms to rebuild form data from the smi.
	 * 
	 * @see refreshTask below
	 * 
	 * @param study_id
	 * @param task_id
	 * @param email
	 * @param passwordMD5
	 */
	public void insertTask(int study_id, int task_id, String email,
			String passwordMD5) {
		FormData fd = new FormData(study_id, task_id, email, passwordMD5);
		Cursor c = getForms(fd);
		if (c != null && c.getCount() > 0) {
			// update
			refreshTask(false, fd);
		} else {
			// insert
			refreshTask(true, fd);
		}
		c.close();
	}

	/**
	 * convenience method to get a task based on all the look up parameters
	 * 
	 * @param study_id
	 * @param task_id
	 * @param email
	 * @param passwordMD5
	 * @return returns cursor to task
	 */
	public Cursor getForms(int study_id, int task_id, String email,
			String passwordMD5) {
		FormData fd = new FormData(study_id, task_id, email, passwordMD5);
		return getForms(fd);
	}

	/**
	 * get the forms field from the drdat_forms table
	 * 
	 * @param fd
	 *            FormData object used to select correct db row
	 * @return a cursor to that row
	 */
	public Cursor getForms(FormData fd) {
		return db.rawQuery("select forms from " + DB_TABLE + " where "
				+ DB_ROWID, fd.args);
	}

	/**
	 * Queries the smi via the interweb to get the latest form data for a given
	 * task. The smi does all the hard work and returns canned html that can be
	 * dumped directly into the WebView in DrdatGUI's DrdatForms activity.
	 * 
	 * @param insert
	 *            if true make a new record, if not, update an existing record
	 * @param fd
	 *            a form data object with all needed form fields
	 */
	private void refreshTask(boolean insert, FormData fd) {
		try {
			URL url = Smi.URL(context, "phone.php?do=getTask" + "&task_id="
					+ fd.task_id + "&study_id=" + fd.study_id + "&email="
					+ fd.email + "&password=" + fd.passwordMD5);
			Log.i(LOG_TAG, "getting url: " + url.toExternalForm());

			BufferedReader in = new BufferedReader(new InputStreamReader(url
					.openStream()));

			String html = "";
			String str = new String();
			while ((str = in.readLine()) != null) {
				html += str + " ";
			}
			in.close();

			ContentValues values = new ContentValues();

			if (insert) {
				Log.i(LOG_TAG, "sqlite: inserting!");
				values.put("forms", html);
				values.put("study_id", fd.study_id);
				values.put("task_id", fd.task_id);
				values.put("email", fd.email);
				values.put("password", fd.passwordMD5);
				db.insert(DB_TABLE, "forms", values);

			} else {
				Log.i(LOG_TAG, "sqlite: updating!");
				values.put("forms", html);
				db.update(DB_TABLE, values, DB_ROWID, fd.args);
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, "refreshTask: " + e.toString() + ": "
					+ e.getMessage());
		}
	}
}
