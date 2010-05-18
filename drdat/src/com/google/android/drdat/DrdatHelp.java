/**
 * 
 */
package com.google.android.drdat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

/**
 * Display the users guide to DRDAT. See also the help.html asset file.
 * 
 * @author cal
 *
 */
public class DrdatHelp extends Activity {
	private final String LOG_TAG = "DRDAT HELP";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.help);
	    WebView local = (WebView) findViewById(R.id.local_help);
	    WebView remote = (WebView) findViewById(R.id.remote_help);
	    try {
		    BufferedReader in = new BufferedReader(new InputStreamReader(getAssets().open("help.html")));
		    String html = "";
		    String buf;
		    while ((buf = in.readLine()) != null) {
		    	html += buf + " ";
		    }
		    in.close();
		    String url = Smi.str(this, "help.php");
		    local.loadData(html, "text/html", "utf-8");

		    // we add this but cannot assume that it exists or is available
		    remote.loadUrl(url);
		    
	    } catch (Exception e) {
	    	Log.e(LOG_TAG,"DrdatHelp onCreate: "+e+": "+e.getMessage());
	    }
	}

}
