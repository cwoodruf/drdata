/**
 * 
 */
package com.google.android.drdat;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Settings editor for drdat. Used to change the smi url and possibly among other things.
 * 
 * @author cal
 *
 */
public class DrdatSettings extends Activity {
	private Activity me;
	private Button save;
	private Button restore;
	private EditText ed;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.settings);
	    me = this;
	    ed = (EditText) findViewById(R.id.smiurl);
	    ed.setText(Smi.str(me));
	    
	    save = (Button) findViewById(R.id.save_settings);
	    restore = (Button) findViewById(R.id.restore_settings);
	    
	    save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Smi.set(me, ed.getText().toString());
				ed.setText(Smi.str(me));
				Toast.makeText(me, R.string.SettingsSaved, Toast.LENGTH_LONG).show();
			}
	    });
	    restore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Smi.reset(me);
				ed.setText(Smi.str(me));
				Toast.makeText(me, R.string.SettingsRestored, Toast.LENGTH_LONG).show();
			}
	    });
	}

}
