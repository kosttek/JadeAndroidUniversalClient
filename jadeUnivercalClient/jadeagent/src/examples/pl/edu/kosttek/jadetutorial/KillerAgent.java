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
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class KillerAgent extends Agent {

	protected void setup() {

		//Register the SL content language
		getContentManager().registerLanguage(new jade.content.lang.sl.SLCodec(),
		FIPANames.ContentLanguage.FIPA_SL0);

		//Register the mobility ontology
		getContentManager().registerOntology(jade.domain.JADEAgentManagement.JADEManagementOntology.getInstance());
		addBehaviour(new KillAgentBehaviour());
	}

	// Put agent clean-up operations here
	protected void takeDown() {
		// Printout a dismissal message
		System.out.println("Buyer-agent " + getAID().getName()
				+ " terminating.");
	}



	private class KillAgentBehaviour extends Behaviour {
		public void action() {
			try{
			jade.domain.JADEAgentManagement.KillAgent ka = new
					jade.domain.JADEAgentManagement.KillAgent();
					        AID kaid = new AID("client_1357168731094", AID.ISLOCALNAME);
					        kaid.addAddresses("http://127.0.0.1:7778/acc");
					        
					        ka.setAgent(kaid);
					        jade.content.onto.basic.Action kaction = new
					jade.content.onto.basic.Action();
					        kaction.setActor(getAMS());
					        kaction.setAction(ka);

					        ACLMessage AMSRequest = new ACLMessage(ACLMessage.REQUEST);
					        AMSRequest.setSender(getAID());
					        AMSRequest.clearAllReceiver();
					        AMSRequest.addReceiver(getAMS());
					AMSRequest.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
					      AMSRequest.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
					     
					AMSRequest.setOntology(jade.domain.JADEAgentManagement.JADEManagementOntology.NAME);
					      getContentManager().fillContent(AMSRequest, kaction);
					      send(AMSRequest);

					    }

					    catch(Exception fe) {

					      fe.printStackTrace();

					    }

		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
	}
}
