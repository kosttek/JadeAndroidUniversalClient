/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
 *****************************************************************/

package examples.pl.edu.kosttek.jadetutorial;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class QueueBuyerAgent extends Agent {
	Integer number;
	Integer queueSize, currentNumberInQ;
	// The list of known queue agents
	private AID[] queueAgents;

	// Put agent initializations here
	protected void setup() {
		System.out.println("Hallo! queueclient-agent " + getAID().getName()
				+ " is ready.");
		addBehaviour(new Behaviour() {
			public void action() {
				// Update the list of seller agents
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType(QueueAgent.SERVER_TYPE);// type = server-queue
				template.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent,
							template);
					System.out.println("Found the following seller agents:");
					queueAgents = new AID[result.length];
					for (int i = 0; i < result.length; ++i) {
						queueAgents[i] = result[i].getName();
						System.out.println(queueAgents[i].getName());
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
		});

	}

	// Put agent clean-up operations here
	protected void takeDown() {
		// Printout a dismissal message
		System.out.println("Buyer-agent " + getAID().getName()
				+ " terminating.");
	}

	private class InfromServerBeh extends Behaviour {
		private int step = 0;

		public void action() {
			switch (step) {
			case 0:
				// Send the cfp to all sellers
				ACLMessage ifo = new ACLMessage(ACLMessage.REQUEST);
				// send info to first queue server
				ifo.addReceiver(queueAgents[0]);
				ifo.setConversationId(QueueAgent.CONID_GETNUMBER);
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
						QueueAgent.CONID_GETNUMBER);
				ACLMessage msg = myAgent.receive(mt);

				if (msg != null) {
					String num = msg.getContent();
					number = Integer.parseInt(num);
					System.out.println("@@@@@" + number);
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

	private class ReceiveInform extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(
					ACLMessage.INFORM).MatchConversationId(
					QueueAgent.CONID_BROADCAST);
			ACLMessage msg = myAgent.receive(mt);

			if (msg != null) {

				try {
					Integer[] content = (Integer[]) msg.getContentObject();
					currentNumberInQ = content[0];
					queueSize = content[1];
				} catch (UnreadableException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(currentNumberInQ + "/" + queueSize);
			} else {
				block();
			}
		}
	}
}
