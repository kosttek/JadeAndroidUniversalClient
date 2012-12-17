package pl.edu.kosttek.jadeclient.ui;



import jade.core.AID;
import jade.core.Agent;
import jade.core.MicroRuntime;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

import java.util.ArrayList;
import java.util.List;

import pl.edu.kosttek.jadeclient.R;
import pl.edu.kosttek.jadeclient.agent.BuyerInterface;
import pl.edu.kosttek.jadeclient.config.Config;
import pl.edu.kosttek.jadeclient.connection.ServerConnection;
import pl.edu.kosttek.jadeclient.receivers.LoadedJarReceiver;
import pl.edu.kosttek.jadeclient.receivers.LoaderAgentBroadcastReceiver;
import pl.edu.kosttek.jadeclient.JadeClient;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class JadeClientActivity extends Activity {
	public static final int FIRST_MENU_ELEMENT = 1;
    String host = Config.jadeServerhost;
    String port = Config.jadeServerPort;
    private LoaderAgentBroadcastReceiver myReceiver;
    private LoadedJarReceiver loadedJarReceiver;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        setContentView(R.layout.main);
        
		DynamicActivity.setSavedViewNull();
        
        ServerConnection serverConnection = ServerConnection.getInstance(this);
        
        View mainView = ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0);       
        myReceiver = new LoaderAgentBroadcastReceiver(this,mainView);
        IntentFilter refreshParticipantsFilter = new IntentFilter();
		refreshParticipantsFilter
				.addAction("pl.edu.kosttek.REFRESH_AGENTS");
		registerReceiver(myReceiver, refreshParticipantsFilter);
		
		loadedJarReceiver = new LoadedJarReceiver(this);
        IntentFilter loadedJarFilter = new IntentFilter();
        loadedJarFilter
				.addAction("pl.edu.kosttek.DYNAMIC_ACTIVITY");
		registerReceiver(loadedJarReceiver, loadedJarFilter);
        
        serverConnection.startConnection(host, port);
        System.out.println("started");
        
    }
    @Override
    protected void onResume() {
    	try{
    		unregisterReceiver(loadedJarReceiver);
    	}catch(IllegalArgumentException e){
    		System.out.println("reveicer not registered");
    	}
    	loadedJarReceiver = new LoadedJarReceiver(this);
        IntentFilter loadedJarFilter = new IntentFilter();
        loadedJarFilter
				.addAction("pl.edu.kosttek.DYNAMIC_ACTIVITY");
		registerReceiver(loadedJarReceiver, loadedJarFilter);
    	super.onResume();
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, FIRST_MENU_ELEMENT, 0, "Settings");
    	return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case 1:
        	Intent i = new Intent(this, SettingsActivity.class);
        	startActivity(i);
            break;
        }
        return true;
    }
    
    @Override
    protected void onStop()
    {
    	
//        unregisterReceiver(myReceiver);
        unregisterReceiver(loadedJarReceiver);
        super.onStop();
    }
    
   
    
}

