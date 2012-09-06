package pl.edu.kosttek.jadebook.ui;

import pl.edu.kosttek.jadebook.JadeAndBookbuy;

import pl.edu.kosttek.jadebook.R;
import pl.edu.kosttek.jadebook.config.Config;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SettingsActivity extends Activity {
	EditText host;
	EditText port;
	EditText name;
	Button button;
	
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		host = (EditText) findViewById(R.id.hostSettings);
		host.setText(Config.jadeServerhost, TextView.BufferType.EDITABLE);
		port = (EditText) findViewById(R.id.portSettings);
		port.setText(Config.jadeServerPort, TextView.BufferType.EDITABLE);
		name = (EditText) findViewById(R.id.agentName);
		name.setText(Config.agentName, TextView.BufferType.EDITABLE);
		
		button = (Button) findViewById(R.id.settingButton);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Config.agentName = name.getText().toString();
				Config.jadeServerhost = host.getText().toString();
				Config.jadeServerPort = port.getText().toString();
				((JadeAndBookbuy)getApplication()).savePreferences();
			}
		});
	};
	
	
}
