package pl.edu.kosttek.jadeclient.receivers;

import jade.core.AID;
import jade.core.MicroRuntime;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

import java.util.ArrayList;
import java.util.List;

import pl.edu.kosttek.jadeclient.R;
import pl.edu.kosttek.jadeclient.agent.LoaderInterface;
import pl.edu.kosttek.jadeclient.connection.ServerConnection;
import pl.edu.kosttek.jadeclient.ui.DynamicActivity;
import pl.edu.kosttek.jadeclient.ui.JadeClientActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class LoaderAgentBroadcastReceiver extends BroadcastReceiver {
	Context context;
	View mainView;
	
	public LoaderAgentBroadcastReceiver(Context context, View mainView) {
		this.context = context;
		this.mainView = mainView;
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.i("reciever", "Received intent " + action);
		if (action.equalsIgnoreCase("pl.edu.kosttek.REFRESH_AGENTS")) {
			refreshAgents();
		}
//		else if (action.equalsIgnoreCase("pl.edu.kosttek.DYNAMIC_ACTIVITY")){
//			Intent intent2 = new Intent(context, DynamicActivity.class);
//			context.startActivity(intent2);
//		}
	}
	
	private void refreshAgents(){
		List<String > list = new ArrayList<String>();
		AgentController ac = null;
		LoaderInterface buyer= null;
		try {
			ac = MicroRuntime.getAgent(ServerConnection.AGENT_NAME);
			buyer = ac.getO2AInterface(LoaderInterface.class);
			
			
		} catch (StaleProxyException e) {
			e.printStackTrace();
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		AID [] agentList = null;
		if(buyer != null){
			DFAgentDescription [] tempTab = buyer.getAgentsOnServer(); 
			agentList = new AID[tempTab.length];
			for (int i = 0; i < tempTab.length; i++) {
				agentList [i] = tempTab [i].getName();
				
			}
			
		}
	
		

		ListView lv = (ListView) mainView.findViewById(R.id.list1);
		lv.setAdapter(new ServerAgentsAdapter(
				context, R.layout.participant,
				agentList));
		lv.setOnItemClickListener(new OnAIDitemClickListener(buyer));
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
	LoaderInterface agent;
	public OnAIDitemClickListener(LoaderInterface agent) {
		this.agent = agent;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View arg1, int position,
			long arg3) {
		AID elem = (AID)parent.getItemAtPosition(position);
		agent.runGetJarBehaviour(elem);
		Log.i("clicko", "clicko");
	}
	
}