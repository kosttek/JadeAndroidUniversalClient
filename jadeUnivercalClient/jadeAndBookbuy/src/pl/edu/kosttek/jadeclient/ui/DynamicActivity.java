package pl.edu.kosttek.jadeclient.ui;


import jade.core.MicroRuntime;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import pl.edu.kosttek.jadeclient.R;
import pl.edu.kosttek.jadeclient.agent.BuyerInterface;
import pl.edu.kosttek.jadeclient.connection.ServerConnection;
import pl.edu.kosttek.jadeclient.dexfileload.DexFileLoader;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

public class DynamicActivity extends Activity {
	static Object view = null;
	protected void onCreate(android.os.Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		
		if(view == null){
			view = (View)tempInit();
		}
		
		if(view != null && view   instanceof View) {
			if(((View) view).getParent()!= null)
				((ViewGroup)((View) view).getParent()).removeAllViews();
			setContentView((View)view);
		}else{
			setContentView(R.layout.dynamic);
		}
			
	};
	
	@Override
	public void onBackPressed() {
		view = null;
		super.onBackPressed();
		 finish();
	}
	
	static public void  setSavedViewNull(){
		view = null;
	}
	
	Object tempInit(){
		Object result = null;
		DexFileLoader dfl = new DexFileLoader(DynamicActivity.this);
		
		AgentController ac;
		try {
			ac = MicroRuntime.getAgent(ServerConnection.AGENT_NAME);

		BuyerInterface bi = ac.getO2AInterface(BuyerInterface.class);
		File file = bi.getTempFile();
		result =  dfl.runInitiator(file, ServerConnection.getInstance(this).getMicroRuntimeServiceBinder(),  ServerConnection.getInstance(this).getAgentStartupCallback());
		} catch (ControllerException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return result;


	}
}
