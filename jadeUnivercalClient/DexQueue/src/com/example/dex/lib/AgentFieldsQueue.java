package com.example.dex.lib;

import jade.core.AID;
import android.os.Handler;
import android.widget.TextView;

public class AgentFieldsQueue {
//	static public AgentFields instance;
	public AgentFieldsQueue(){
		
	}
//	static public AgentFields getInstance(){
//		if(instance == null){
//			instance =  new AgentFields();
//		}
//		return instance;
//	}
	public Integer number;
	public Integer queueSize, currentNumberInQ;
	// The list of known queue agents
	public AID[] queueAgents;
	public Handler handler;
	public TextView infoAmount, yourNumber;

}
