package com.example.dex.lib;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import pl.edu.kosttek.jadebook.agent.AddBehaviourInterface;
import pl.edu.kosttek.jadebook.agent.EmptyAgent;
import android.util.Log;

public class GetSellerAgentsBehaviour extends TickerBehaviour {
	AgentFields fields;
	long tick;
	AddBehaviourInterface addBehaviourInterface;

	public GetSellerAgentsBehaviour(Agent agent, long tick, AgentFields fields) {
		super(agent, tick);

		this.tick = tick;
		this.addBehaviourInterface = addBehaviourInterface;
	}

	public void onTick() {
		this.fields = (AgentFields) ((EmptyAgent) myAgent).getFields();

		System.out.println("Trying to buy " + fields.targetBookTitle);
		// Update the list of seller agents
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("book-selling");
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(myAgent, template);
			System.out.println("Found the following seller agents:");
			fields.sellerAgents = new AID[result.length];
			for (int i = 0; i < result.length; ++i) {
				fields.sellerAgents[i] = result[i].getName();
				System.out.println(fields.sellerAgents[i].getName());
			}
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		// Perform the request
		Log.e("", "---------ADDADD " + fields.sellerAgents);
		myAgent.addBehaviour(new RequestPerformer(fields));
		try {
			Thread.currentThread().sleep(tick);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
