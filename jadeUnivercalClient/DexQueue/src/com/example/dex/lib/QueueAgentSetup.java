package com.example.dex.lib;

import android.graphics.Color;
import pl.edu.kosttek.jadeclient.agent.EmptyAgent;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;


public class QueueAgentSetup  extends Behaviour{
	public static final String SERVER_TYPE = "queue-server";
	public static final String CONID_GETNUMBER = "info_for_server";
	public static final String CONID_BROADCAST = "info_from_server_broadcast";
	
	AgentFieldsQueue fields;
	public void action() {
		fields = (AgentFieldsQueue)((EmptyAgent)myAgent).getFields();
		// Update the list of seller agents
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(SERVER_TYPE);// type = server-queue
		template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(myAgent,
					template);
			System.out.println("Found the following seller agents:");
			fields.queueAgents = new AID[result.length];
			for (int i = 0; i < result.length; ++i) {
				fields.queueAgents[i] = result[i].getName();
				System.out.println(fields.queueAgents[i].getName());
			}
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		// Perform the request
		myAgent.addBehaviour(new ReceiveInform());
		myAgent.addBehaviour(new InfromServerBeh());
	}

	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return true;
	}
}
 class InfromServerBeh extends Behaviour {
	private int step = 0;
	AgentFieldsQueue fields;

	public void action() {
		fields = (AgentFieldsQueue)((EmptyAgent)myAgent).getFields();
		switch (step) {
		case 0:
			// Send the cfp to all sellers
			ACLMessage ifo = new ACLMessage(ACLMessage.REQUEST);
			// send info to first queue server
			ifo.addReceiver(fields.queueAgents[0]);
			ifo.setConversationId(QueueAgentSetup.CONID_GETNUMBER);
			ifo.setReplyWith("info" + System.currentTimeMillis()); // Unique
																	// value
			myAgent.send(ifo);
			// Prepare the template to get proposals

			step = 1;
			block();

			break;
		case 1:
			MessageTemplate mt = MessageTemplate.MatchPerformative(
					ACLMessage.REQUEST).MatchConversationId(
							QueueAgentSetup.CONID_GETNUMBER);
			ACLMessage msg = myAgent.receive(mt);

			if (msg != null) {
				String num = msg.getContent();
				fields.number = Integer.parseInt(num);
				System.out.println("@@@@@" +fields.number);
				step = 2;
				break;
			}
		}

	}

	public boolean done() {
		if (step == 2)
			return true;
		return false;
	}
}

class ReceiveInform extends CyclicBehaviour {
	AgentFieldsQueue fields;
	public void action() {
		fields = (AgentFieldsQueue)((EmptyAgent)myAgent).getFields();
		MessageTemplate mt = MessageTemplate.MatchPerformative(
				ACLMessage.INFORM).MatchConversationId(
						QueueAgentSetup.CONID_BROADCAST);
		ACLMessage msg = myAgent.receive(mt);

		if (msg != null) {

			try {
				Integer[] content = (Integer[]) msg.getContentObject();
				fields.currentNumberInQ = content[0];
				fields.queueSize = content[1];
			} catch (UnreadableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(fields.currentNumberInQ + "/" + fields.queueSize);
			
			fields.handler.post(new Runnable() {
				
				public void run() {
					fields.infoAmount.setText(fields.currentNumberInQ+"/"+fields.queueSize);
					if(fields.currentNumberInQ.equals(fields.number)){
						fields.yourNumber.setTextColor(Color.GREEN);
					}else{
						fields.yourNumber.setTextColor(Color.RED);
					}
					fields.yourNumber.setText(fields.number+"");
				}
			});
		} else {
			block();
		}
	}
}


