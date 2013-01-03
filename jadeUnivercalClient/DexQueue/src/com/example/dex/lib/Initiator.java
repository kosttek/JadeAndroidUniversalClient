package com.example.dex.lib;

import jade.android.MicroRuntimeServiceBinder;
import jade.android.RuntimeCallback;
import jade.core.Agent;
import jade.core.MicroRuntime;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import pl.edu.kosttek.jadeclient.agent.EmptyAgent;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class Initiator {
	private static String AGENT_NAME = "lupus"+System.currentTimeMillis();
	TextView infoAmount, yourNumber;
	Handler handler = new Handler();;
	String bookTitleFromDialog = null;

	private MicroRuntimeServiceBinder microRuntimeServiceBinder;
	private RuntimeCallback<AgentController> agentStartupCallback;
	private Context context;

	public View init(MicroRuntimeServiceBinder mrsb,
			RuntimeCallback<AgentController> rcac, final Context context) {
		this.microRuntimeServiceBinder = mrsb;
		this.agentStartupCallback = rcac;
		this.context = context;
		handler = new Handler();
		View view = createLayout();
		startEmptyAgent(AGENT_NAME, setOnLoadListener());
//		showTestDialog();

		
		
		
		return view;

	}

	public EmptyAgent.OnEmptyAgentLoad setOnLoadListener() {
		EmptyAgent.OnEmptyAgentLoad onLoadAgentListener = new EmptyAgent.OnEmptyAgentLoad() {

		public void setup(Agent agent) {
				
				AgentFieldsQueue af = new AgentFieldsQueue();
				
				
				((EmptyAgent)agent).setFields(af);
				af.handler = handler;
				af.infoAmount = infoAmount;
				af.yourNumber = yourNumber;
				agent.addBehaviour(new QueueAgentSetup());
				// try {
				// AgentController ac;
				// ac = MicroRuntime.getAgent(AGENT_NAME);
				// AddBehaviourInterface ea;
				// do{
				// ea = ac.getO2AInterface(AddBehaviourInterface.class);
				// Log.e("not err", "ea " + ea);
				// }while(ea == null);
				//
				// AgentFields.getInstance().targetBookTitle="book2";
				// ea.addBehaviour(new GetSellerAgentsBehaviour(null,
				// 10000,ea));
				//
				// } catch (IllegalArgumentException e) {
				// e.printStackTrace();
				// } catch (ControllerException e) {
				// e.printStackTrace();
				// }
			}
		};
		return onLoadAgentListener;
	}

	public void startEmptyAgent(final String nickname,
			EmptyAgent.OnEmptyAgentLoad onEmptyAgentLoad) {
		final String agentClassName = EmptyAgent.class.getName();
		// final String agentClassName =
		// "pl.edu.kosttek.jadebook.agent.EmptyAgent";
		if (getMicroRuntimeServiceBinder().pingBinder()) {
			getMicroRuntimeServiceBinder().startAgent(nickname, agentClassName,
					new Object[] { context, onEmptyAgentLoad },
					new RuntimeCallback<Void>() {
						@Override
						public void onSuccess(Void thisIsNull) {
							Log.e("", "Successfully start of the "
									+ agentClassName + "...");
							try {
								agentStartupCallback.onSuccess(MicroRuntime
										.getAgent(nickname));
							} catch (ControllerException e) {
								// Should never happen
								agentStartupCallback.onFailure(e);
							}
						}

						@Override
						public void onFailure(Throwable throwable) {
							Log.i("", "Failed to start the " + agentClassName
									+ "...");
							agentStartupCallback.onFailure(throwable);
						}
					});
		}
	}

	void showTestDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);

		alert.setTitle("Book Buyer Interface");
		alert.setMessage("Choose your book title which you want to buy");

		final EditText input = new EditText(context);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				bookTitleFromDialog = input.getText().toString();
				startEmptyAgent(AGENT_NAME, setOnLoadListener());
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				startEmptyAgent(AGENT_NAME, setOnLoadListener());
		  }
		});

		alert.show();
	}

	private View createLayout() {
		LinearLayout lLayout = new LinearLayout(context);
		lLayout.setOrientation(LinearLayout.VERTICAL);
		// -1(LayoutParams.MATCH_PARENT) is fill_parent or match_parent since
		// API level 8
		// -2(LayoutParams.WRAP_CONTENT) is wrap_content
		lLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		infoAmount = new TextView(context);
		infoAmount.setTextColor(Color.WHITE);
		infoAmount.setTextSize(25);
		infoAmount.setText("N/N");
		infoAmount.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		infoAmount.setGravity(Gravity.CENTER_HORIZONTAL);
		yourNumber = new TextView(context);
		yourNumber.setTextColor(Color.GRAY);
		yourNumber.setTextSize(50);
		yourNumber.setText("N");
		yourNumber.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		yourNumber.setGravity(Gravity.CENTER_HORIZONTAL);
		lLayout.addView(infoAmount);
		lLayout.addView(yourNumber);
		return lLayout;
	}

	public MicroRuntimeServiceBinder getMicroRuntimeServiceBinder() {
		return microRuntimeServiceBinder;
	}

	public void setMicroRuntimeServiceBinder(
			MicroRuntimeServiceBinder microRuntimeServiceBinder) {
		this.microRuntimeServiceBinder = microRuntimeServiceBinder;
	}
}
