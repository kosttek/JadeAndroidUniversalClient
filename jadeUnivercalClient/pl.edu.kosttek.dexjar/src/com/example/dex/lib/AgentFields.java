package com.example.dex.lib;

import jade.core.AID;
import android.os.Handler;
import android.widget.TextView;

public class AgentFields {
	static public AgentFields instance;
	private AgentFields(){
		
	}
	static public AgentFields getInstance(){
		if(instance == null){
			instance =  new AgentFields();
		}
		return instance;
	}
	public String targetBookTitle;
	public AID[] sellerAgents;
	public Handler handler;
	public TextView textView;
    public int test = 0;
}
