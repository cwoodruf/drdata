package com.google.android.drdat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

/**
 * Does the job of taking form html snippets and turning them into
 * actual web pages that can be displayed by a web view. Uses the 
 * DrdatCL DrdatTasks content provider to get the form bodies.
 * 
 * @author cal
 *
 */
public class DrdatFormCache {
	private final String LOG_TAG = "DRDAT FORM CACHE";
	private int study_id;
	private int task_id;
	private String[] forms;
	private int currForm;
	private String mime = "text/html";
	private String encoding = "utf-8";
	private String htmlstart;
	private String htmlend;
	private Context context;

	public DrdatFormCache(Context ctx, int sid, int tid) {
		context = ctx;
		task_id = tid;
		study_id = sid;
		getForms();
	}

	/**
	 * get the javascript and other resources (eg css) needed to start the html
	 * page we cannot use references to outside resources as loadData will fail
	 * to load them for us
	 * 
	 * @return the start portion of the WebView html
	 */
	public String getHtmlstart() {
		if (htmlstart == null) {

			String js = new String();

			AssetManager am = context.getAssets();
			try {
				// we need this javascript to save entered form data
				BufferedReader jsin = new BufferedReader(new InputStreamReader(
						am.open("forms.js")));
				String str;
				while ((str = jsin.readLine()) != null) {
					js += str;
				}
				jsin.close();

				// for the loadData method to work we have to load any
				// javascript and css as a string
				// we can't refer to external files as a security precaution -
				// not sure how helpful that is
				htmlstart = "<html><head><script language=\"javascript\">"
						+ js
						+ "</script></head><body onload=\"fill(document.inputForm);\" >"
						+ "<form name=\"inputForm\" action=\"javascript:void(0);\" onSubmit=\"save(this);\">\n";
			} catch (Exception e) {
				Log.e(LOG_TAG, e.getMessage());
			}

		}
		return htmlstart;
	}

	/**
	 * make the button bar
	 * 
	 * @param formId
	 *            which form we are looking at
	 * @return html string with appropriate buttons
	 */
	public String getHtmlend() {
		String submit = "<input type=submit onClick=\"saveaction(this);\" ";
		Log.d(LOG_TAG,"forms "+forms.length+" currform "+currForm);
		if (forms.length > 0) {
			if (currForm >= forms.length-2) {
				if (currForm <= 0) htmlend = submit+" value=\"save data\">";
				else htmlend = submit+" value=\"&lt; prev\"> "+submit+" value=\"save data\">";
			} else if (currForm <= 0) {
					htmlend = "&lt; prev "+submit+" value=\"next &gt;\">";
			} else {
				htmlend = submit+" value=\"&lt; prev\"> "+submit+" value=\"next &gt;\">";
			}
		}
		htmlend += "</form></body></html>";
		return htmlend;
	}

	/**
	 * generate all the html for the WebView sews together the htmlstart current
	 * form and htmlend strings will choke if you haven't set up the forms or
	 * there aren't any forms
	 * 
	 * @param currForm
	 * @return html string for the WebView
	 * @throws ArrayIndexOutOfBoundsException
	 */
	public String generate() throws ArrayIndexOutOfBoundsException {
		getForms();
		if (currForm < 0 || currForm >= forms.length)
			throw new ArrayIndexOutOfBoundsException();
		String html = getHtmlstart() + forms[currForm] + getHtmlend();
		return html;
	}

	/**
	 * Grab the forms from the drdat cl content provider.
	 * This fills in the forms class member.
	 */
	private void getForms() {
		if (forms != null) return;
		try {
	
			Uri task_url = Uri.parse(context.getString(R.string.TaskUrl));
			Cursor c = context.getContentResolver().query(
					task_url, 
					new String[] { "forms" }, 
					null, // would normally be "where" part of the query string 
					new String[] { 
							Integer.toString(study_id), 
							Integer.toString(task_id),
							Login.getLastEmail(),
							Login.getLastPasswordMD5()
					},
					null
				);
			
			forms = null;
			currForm = -1;
			if (c.moveToFirst()) {
				String raw = c.getString(0);
				if (!raw.matches("^ERROR.*")) { 
					forms = c.getString(0).split("<!-- split -->");
					currForm = 0;
					c.close();
				}
			} 

		} catch (Exception e) {
			Log.e(LOG_TAG,"getForms: "+e.toString()+": "+e.getMessage());
		}
	}

	// methods for moving through forms
	public void prevForm() {
		if (currForm > 0) currForm--;
		else currForm = 0;
	}

	public void nextForm() {
		if (currForm < forms.length-1) currForm++;
		else currForm = forms.length-1;
	}

	// automatically generated getters and setters
	public void setStudy_id(int study_id) {
		this.study_id = study_id;
	}

	public int getStudy_id() {
		return study_id;
	}

	public void setTask_id(int task_id) {
		this.task_id = task_id;
	}

	public int getTask_id() {
		return task_id;
	}

	public void setCurrForm(int currForm) {
		this.currForm = currForm;
	}

	public int getCurrForm() {
		return currForm;
	}

	public void setMime(String mime) {
		this.mime = mime;
	}

	public String getMime() {
		return mime;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getEncoding() {
		return encoding;
	}
	
	public int numForms() {
		if (forms == null) return 0;
		return forms.length;
	}
}
