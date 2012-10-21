package pl.edu.kosttek.jadebook.ui;


import jade.core.MicroRuntime;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import pl.edu.kosttek.jadebook.R;
import pl.edu.kosttek.jadebook.agent.BuyerInterface;
import pl.edu.kosttek.jadebook.connection.ServerConnection;
import pl.edu.kosttek.jadebook.dexfileload.DexFileLoader;
import android.app.Activity;
import android.view.View;

public class DynamicActivity extends Activity {
	protected void onCreate(android.os.Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		
		Object view = tempInit();
		if(view != null && view   instanceof View) {
			setContentView((View)view);
		}else{
			setContentView(R.layout.dynamic);
		}
			
	};
	
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
		}
		return result;
//		ServerConnection.getInstance(this).startEmptyAgent("mentos");
//		AgentController ac;
//
//			
//		
//			
//			
//			
//			ServerConnection.getInstance(this).setOnLoadedAgentListener(new OnLoadedAgentListener() {
//				
//				@Override
//				public void loaded() {
//					try {
//					AgentController ac;
//					ac = MicroRuntime.getAgent("mentos");
//					AddBehaviourInterface ea;
//					do{
//					ea = ac.getO2AInterface(AddBehaviourInterface.class);
//					Log.e("not err", "ea " + ea);
//					}while(ea == null);
//					ac = MicroRuntime.getAgent(ServerConnection.AGENT_NAME);
//					BuyerInterface bi = ac.getO2AInterface(BuyerInterface.class);
//					File file = bi.getTempFile();
//					AID [] sellerAgents  = new AID [bi.getAgentsOnServer().length];
//					for(int i = 0 ;i < bi.getAgentsOnServer().length; i++){
//						sellerAgents[i] = bi.getAgentsOnServer()[i].getName();
//					}
//					
//					DexFileLoader dfl = new DexFileLoader(DynamicActivity.this);
//					dfl.setParams(sellerAgents, "book");
//					
//					ea.addBehaviour(dfl.getBehaviour(file, null));
//					
//					} catch (IllegalArgumentException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (SecurityException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (InstantiationException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (IllegalAccessException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (InvocationTargetException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (NoSuchMethodException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (ControllerException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			});
//			
//			
//		

	}
}
