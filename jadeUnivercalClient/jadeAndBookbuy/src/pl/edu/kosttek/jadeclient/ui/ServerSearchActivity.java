package pl.edu.kosttek.jadeclient.ui;

import pl.edu.kosttek.jadeclient.R;
import pl.edu.kosttek.jadeclient.config.Config;
import pl.edu.kosttek.jadeclient.connection.OnServerFoundListener;
import pl.edu.kosttek.jadeclient.connection.ServerSearching;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ServerSearchActivity extends Activity {

	OnServerFoundListener onServerFoundListener = new OnServerFoundListener() {
		
		@Override
		public void found(String addressport) {
			Config.jadeServerhost = addressport.split(":")[0].substring(1);
			Config.jadeServerPort = addressport.split(":")[1];
			Intent intent = new Intent(ServerSearchActivity.this, JadeClientActivity.class);
			startActivity(intent);
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_search);
        ServerSearching.findJadeServer(onServerFoundListener);
        initButton();
    }
    
    void initButton(){
    	Button button = (Button) findViewById(R.id.button1);
    	button.setText("set host manually");
    	button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ServerSearchActivity.this, SettingsActivity.class);
				startActivity(intent);
				
			}
		});
    }

}
