package com.google.android.drdat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

/**
 * Manage email / password pairs. We save logins to a db which might be useful for
 * getting more detailed usage statistics later. Note that a participant is 
 * identified by *both* their email and password. The email / password pair
 * is unique to that participant and study. While this behavior may change in
 * the future it makes it much easier to identify participants who are in more
 * than one study (eg software testers).
 * 
 * TODO: do better checking and maybe optionally turn off saving password
 * TODO: how to encrypt information?
 * 
 * @author cal
 *
 */

public class Login {
	private String email;
	private static String lastEmail;
	public static final String EMAIL_PAT = 
		"[a-zA-Z0-9_][a-zA-Z0-9\\.\\-_]*@[a-zA-Z0-9][a-zA-Z0-9\\.\\-]*\\.[a-zA-Z]{2,4}";
	private String password;
	private static String lastPassword;
	public static final int MIN_PW_LEN = 6;
	private String passwordMD5;
	private static String lastPasswordMD5;
	public static final int MD5_LEN = 32;
	private String url;
	private String LOG_TAG = "DRDAT LOGIN CACHE";
	
	/**
	 * from the notepad tutorial - interface to our task and scheduling db
	 * 
	 * {@linkplain http://developer.android.com/resources/tutorials/notepad/codelab/NotepadCodeLab.zip}
	 * {@linkplain http://developer.android.com/resources/tutorials/notepad/index.html
	 * 
	 * @author android 
	 *
	 */
	private static String DB_NAME = "drdat_login";
	private static int DB_VERSION = 2;
	private static String DB_TABLE = "drdat_login";
	private static String DB_CREATE = 
		"create table " + DB_TABLE + " (login_id integer auto_increment, " + 
		"email varchar(64), password varchar(64), passwordMD5 varchar(" + MD5_LEN + "), " +
		"ts datetime default current_timestamp)";
	
	private final class DBHelper extends SQLiteOpenHelper {

		DBHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("drop table if exists "+DB_TABLE);
			db.execSQL(DB_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if (oldVersion != newVersion) {
				db.execSQL("drop table if exists "+DB_TABLE);
				db.execSQL(DB_CREATE);
			}
		}
	}
	
	private SQLiteDatabase db;
	private DBHelper dbh;

	/**
	 * constructor that grabs the last email / password from the db
	 * 
	 * @param ctx context
	 */
	public Login(Context ctx) {
		init(ctx);
		Cursor cur = getCursor(); // just get the latest login
		if (cur != null && cur.getCount() > 0 && cur.moveToFirst()) {
			setEmail(cur.getString(cur.getColumnIndex("email")));
			setPassword(cur.getString(cur.getColumnIndex("password")));
		}
	}
	
	/**
	 * constructor that gets the email and password directly
	 * 
	 * @param ctx context
	 * @param em email
	 * @param pw password
	 */
	public Login(Context ctx, String em, String pw) {
		setEmail(em);
		setPassword(pw);
		init(ctx);
	}
	
	/**
	 * set up the database and the url to the external smi server
	 * 
	 * @param ctx context
	 * @return true if we managed to get a valid db handle and url
	 */
	public boolean init(Context ctx) {
		try {
			url = Smi.str(ctx);
			db = (dbh = new DBHelper(ctx)).getWritableDatabase();
		} catch (Exception e) {
			Log.e(LOG_TAG,"init: "+e.toString()+": "+e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * turn off the database - still get annoying stack dumps even with this 
	 */
	protected void finalize() {
		try {
			db.close();
			dbh.close();
		} catch (Exception e) {
			// ignore
		}
	}

	/**
	 * Saves the email, password and passwordMD5 hash of the password to the drdat_login db
	 * 
	 * @throws DrdatLoginException
	 */
	public void save() throws DrdatLoginException {
		if (email.length() == 0) throw new DrdatLoginException("save: no email!");
		if (!email.matches(EMAIL_PAT)) throw new DrdatLoginException("save: bad email format");
		if (password.length() < MIN_PW_LEN) throw new DrdatLoginException("save: password too short");

		ContentValues values = new ContentValues();
		values.put("email", email);
		values.put("password", password);
		values.put("passwordMD5", passwordMD5);
		try {
			db.insert(DB_TABLE, null, values);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Check an email and md5 hash of the password against the record in the smi.
	 * Done more as a courtesy to make sure that the participant entered them correctly.
	 * This can handle the smi not being available and will instead check against the last
	 * email / password used.
	 * 
	 * @return true if the email and password are of an active participant
	 */
	public boolean validate() {
		boolean valid = false;
		try {
			URL validator = new URL(
					url += "phone.php?do=validateLogin&email="+Uri.encode(email)+"&password="+Uri.encode(passwordMD5));
			BufferedReader in = new BufferedReader(new InputStreamReader(validator.openStream()));
			String buf = in.readLine();
			if (buf.matches("OK.*")) valid = true;
			Log.i(LOG_TAG, "validate: "+buf+" ("+validator+")");
			in.close();
			return valid;
		} catch (Exception e) {
			Log.e(LOG_TAG,"validate: "+e.toString()+": "+e.getMessage());
			Cursor c = db.rawQuery(
					"select passwordMD5 from "+DB_TABLE+" where email=? and password=? limit 1",
					new String [] {email, password}
				);
			if (c.getCount() > 0) return true;
		}
		return false;
	}
	
	public Cursor retrieveLastLogin() {
		return getCursor(null,null);
	}
	
	public Cursor getCursor() {
		return getCursor(email,passwordMD5);
	}
	
	/**
	 * Gets the last login of either a given participant or any participant 
	 * if no email and password given.
	 * 
	 * @param em email
	 * @param pw password
	 * @return cursor with latest database record of that participant or any participant
	 */
	public Cursor getCursor(String em, String pw) {
		String where = null;
		String[] whereData = null;
		if (em != null && pw != null) {
			where = " email=? and passwordMD5=? ";
			whereData = new String[] { em, pw };
		} 
		// grab the most recent login 
		Cursor cur = null;
		try {
			cur = db.query(
					DB_TABLE,
					new String[] { "email", "password", "passwordMD5" }, 
					where, whereData,
					null, null,
					/* order by */ "ts desc", /* limit */ "1"
				);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cur;
	}
	
	/**
	 * Delete everything: used for cleaning a phone up for another user
	 * @see DrdatSmi2TaskList.deleteEverything()
	 */
	public static void deleteEverything(Context context) {
		Login c = new Login(context);
		try {
			c.db.execSQL("delete from "+DB_TABLE);
			
		} catch (Exception e) {
			Log.e(c.LOG_TAG,"DrdatLoginCache.deleteEverything: "+e+": "+e.getMessage());
		}
	}
	
	public void setPassword(String password) {
		Login.lastPassword = this.password = password;
		Login.lastPasswordMD5 = this.passwordMD5 = PasswordEncoder.encode(password);
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getPasswordMD5() {
		return passwordMD5;
	}
	
	public void setEmail(String email) {
		Login.lastEmail = this.email = email;
	}
	
	public String getEmail() {
		return email;
	}
	
	public static String getLastEmail() {
		return lastEmail;
	}
	public static String getLastPassword() {
		return lastPassword;
	}
	public static String getLastPasswordMD5() {
		return lastPasswordMD5;
	}
}
