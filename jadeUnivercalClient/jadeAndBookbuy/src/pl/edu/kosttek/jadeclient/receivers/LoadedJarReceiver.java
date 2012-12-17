package pl.edu.kosttek.jadeclient.receivers;

import pl.edu.kosttek.jadeclient.ui.DynamicActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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

