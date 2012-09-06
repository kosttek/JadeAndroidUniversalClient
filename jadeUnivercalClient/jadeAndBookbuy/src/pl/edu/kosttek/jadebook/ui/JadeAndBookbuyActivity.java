package pl.edu.kosttek.jadebook.ui;



import jade.core.AID;
import jade.core.Agent;
import jade.core.MicroRuntime;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

import java.util.ArrayList;
import java.util.List;

import pl.edu.kosttek.jadebook.R;
import pl.edu.kosttek.jadebook.agent.BuyerInterface;
import pl.edu.kosttek.jadebook.connection.ServerConnection;
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

public class JadeAndBookbuyActivity extends Activity {
	public static final int PIERWSZY_ELEMENT = 1;
    String host = "10.10.0.1";
    String port = "1099";
    private MyReceiver myReceiver;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
        ServerConnection serverConnection = new ServerConnection(this);
 
        myReceiver = new MyReceiver();
        IntentFilter refreshParticipantsFilter = new IntentFilter();
		refreshParticipantsFilter
				.addAction("pl.edu.kosttek.REFRESH_AGENTS");
		registerReceiver(myReceiver, refreshParticipantsFilter);
        
        serverConnection.startConnection(host, port);
        System.out.println("started");
        
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, PIERWSZY_ELEMENT, 0, "Settings");
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
    

    private class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.i("reciever", "Received intent " + action);
			if (action.equalsIgnoreCase("pl.edu.kosttek.REFRESH_AGENTS")) {
				List<String > list = new ArrayList<String>();
				AgentController ac = null;
				BuyerInterface buyer= null;
				try {
					ac = MicroRuntime.getAgent(ServerConnection.AGENT_NAME);
					buyer = ac.getO2AInterface(BuyerInterface.class);
					
					
				} catch (StaleProxyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ControllerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				AID [] agentList = null;
				if(buyer != null){
					DFAgentDescription [] tempTab = buyer.getServerAgents(); 
					agentList = new AID[tempTab.length];
					for (int i = 0; i < tempTab.length; i++) {
						agentList [i] = tempTab [i].getName();
						
					}
					
				}
			
				
				
				ListView lv = (ListView) findViewById(R.id.list1);
				lv.setAdapter(new ServerAgentsAdapter(
						JadeAndBookbuyActivity.this, R.layout.participant,
						agentList));
				lv.setOnItemClickListener(new OnAIDitemClickListener(buyer));
			}
		}
	}
    
    class ServerAgentsAdapter extends ArrayAdapter<AID> {
    	AID[] elements;
    	Context context;
		public ServerAgentsAdapter(Context context, int textViewResourceId,
				AID[] objects) {
			super(context, textViewResourceId, objects);
			elements = objects;
			this.context = context;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView view = new TextView(context);
			view.setText(elements[position].getName());
			view.setTextSize(20);
			return view;
		}
		@Override
		public AID getItem(int position) {
			return elements[position];
		}
    }

    class OnAIDitemClickListener implements OnItemClickListener{
    	BuyerInterface agent;
    	public OnAIDitemClickListener(BuyerInterface agent) {
    		this.agent = agent;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View arg1, int position,
				long arg3) {
			AID elem = (AID)parent.getItemAtPosition(position);
			agent.getBehaviour(elem);
			Log.i("clicko", "clicko");
		}
    	
    }
   
    
}

