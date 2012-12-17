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
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;

public class QueueAgent extends Agent {
	public static final String SERVER_TYPE = "queue-server";
	public static final String CONID_GETNUMBER = "info_for_server";
	public static final String CONID_BROADCAST = "info_from_server_broadcast";
	public static final String INZYNIERKA_PATH = "/home/kosttek/Studia/inz/jade";
	public static final String DEX_CAT = "queue";
	
	private OnDataChange onDataChange;
	private int current=0;
	private int queueSize=0;
	private HashMap<Integer,AID> queueMap = new HashMap<Integer, AID>();
	// The catalogue of books for sale (maps the title of a book to its price)
	private Hashtable catalogue;
	// The GUI by means of which the user can add books in the catalogue
	private QueueGui myGui;

	// Put agent initializations here
	protected void setup() {
		// Create the catalogue
		catalogue = new Hashtable();

		// Create and show the GUI
		myGui = new QueueGui(this);
		myGui.showGui();
		onDataChange = myGui.getOnDataChange();
		
		// Register the book-selling service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("queue-server");
		sd.setName("JADE-queue");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		
		System.out.println("==start==queue ");
		addBehaviour(new PushJarWithDexes());
		addBehaviour(new InformServer());
		File file = new File(".");
		System.out.println("|==========>" + file.getAbsolutePath());
	}


	protected void takeDown() {
		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		myGui.dispose();
		System.out.println("Seller-agent " + getAID().getName()
				+ " terminating.");
	}

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}
	
	public int getQueueSize() {
		return queueSize;
	}

	public void setQueueSize(int queueSize) {
		this.queueSize = queueSize;
	}

	public HashMap<Integer,AID> getQueueMap() {
		return queueMap;
	}

	public void setQueueMap(HashMap<Integer,AID> queueMap) {
		this.queueMap = queueMap;
	}

	public void incrementCurrent(){
		if(getCurrent()<getQueueSize()){
			setCurrent(getCurrent()+1);
			onDataChange.change();
			addBehaviour(new InfoBroadcast());
		}
	}
	
	private class InfoBroadcast extends Behaviour {

		public void action() {

				// Send the cfp to all sellers 
			
				ACLMessage ifo = new ACLMessage(ACLMessage.INFORM);
				// send info to first queue server
				for(Integer key : getQueueMap().keySet())
					ifo.addReceiver(getQueueMap().get(key));
				ifo.setConversationId(QueueAgent.CONID_BROADCAST);
				ifo.setReplyWith("info" + System.currentTimeMillis()); // Unique
																	// value
				Integer [] s = {getCurrent(), getQueueSize()};
				try {
					ifo.setContentObject(s);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				myAgent.send(ifo);
				// Prepare the template to get proposals

	
				block();

		}

		public boolean done() {
				return true;
		}
	} 

	private class InformServer extends CyclicBehaviour {
		public void action() {
			System.out.println("-inform server");
			MessageTemplate mt = MessageTemplate
					.MatchPerformative(ACLMessage.REQUEST)
					.MatchConversationId(CONID_GETNUMBER);
			ACLMessage msg = myAgent.receive(mt);

			if (msg != null ) {

				String title = msg.getContent();
				
				setQueueSize(getQueueSize() + 1);
				getQueueMap().put(getQueueSize(),msg.getSender());
				System.out.println("==^"+getQueueSize());
				ACLMessage reply = msg.createReply();
				reply.setContent(getQueueSize()+"");
				myAgent.send(reply);
				onDataChange.change();
				addBehaviour(new InfoBroadcast());
			} else {
				block();
			}
		}
	} 
	
	interface OnDataChange{
		public void change();
	}
	
	private class PushJarWithDexes extends CyclicBehaviour {
		public void action() {
			//
			MessageTemplate mt = MessageTemplate
					.MatchPerformative(ACLMessage.REQUEST).MatchConversationId("get-jar");
			ACLMessage msg = myAgent.receive(mt);
			System.out.println("pushjar");
			if (msg != null) {

				File file = new File(INZYNIERKA_PATH+"/"+DEX_CAT+"/secondary_dex.jar");
				if(!file.exists())
					file = new File(INZYNIERKA_PATH+"/"+DEX_CAT+"/secondary_dex.jar");
				FileInputStream fis;
				byte[] byteSequenceContent;

				try {

					fis = new FileInputStream(file);
					
					byteSequenceContent = new byte[(int) file.length()];
					fis.read(byteSequenceContent);
					ACLMessage reply = msg.createReply();
					reply.setByteSequenceContent(byteSequenceContent);
					System.out.println("==========>" + file.getAbsolutePath());
					myAgent.send(reply);

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				block();
			}
		}
	}
}

