package pl.edu.kosttek.jadebook.receivers;

import jade.core.AID;
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
import pl.edu.kosttek.jadebook.ui.DynamicActivity;
import pl.edu.kosttek.jadebook.ui.JadeAndBookbuyActivity;

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

public class LoadedJarReceiver extends BroadcastReceiver {
	Context context;
	
	public LoadedJarReceiver(Context context) {
		this.context = context;

	}
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.i("reciever", "loadedjar Received intent " + action);
		if (action.equalsIgnoreCase("pl.edu.kosttek.DYNAMIC_ACTIVITY")){
			Intent intent2 = new Intent(context, DynamicActivity.class);
			context.startActivity(intent2);
		}
	}
	
}

