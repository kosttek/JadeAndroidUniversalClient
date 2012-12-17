package pl.edu.kosttek.jadeclient.ui;


import pl.edu.kosttek.jadeclient.R;
import pl.edu.kosttek.jadeclient.config.Config;
import pl.edu.kosttek.jadeclient.JadeClient;
import android.app.Activity;
import android.content.Intent;
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
				((JadeClient)getApplication()).savePreferences();
				gotoJadeClientActivity();
			}
		});
	};
	@Override
	public void onBackPressed() {
		gotoJadeClientActivity();
		super.onBackPressed();
	}
	
	void gotoJadeClientActivity(){
		Intent intent = new Intent(this, JadeClientActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
}
