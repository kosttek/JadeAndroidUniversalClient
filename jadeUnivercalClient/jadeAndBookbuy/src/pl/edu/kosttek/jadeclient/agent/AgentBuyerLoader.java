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

package pl.edu.kosttek.jadeclient.agent;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.lang.reflect.InvocationTargetException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import dalvik.system.DexClassLoader;

public class AgentBuyerLoader extends Agent implements BuyerInterface {

	// The title of the book to buy
	public String targetBookTitle;
	// The list of known seller agents
	public AID[] sellerAgents;
	private DFAgentDescription[] serverAgents;
	Context context;
	private File tempFile;

	// Put agent initializations here
	protected void setup() {
		// Printout a welcome message
		System.out.println("Hallo! Buyer-agent " + getAID().getName()
				+ " is ready.");
		registerO2AInterface(BuyerInterface.class, this);
		// Get the title of the book to buy as a start-up argument
		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			context = (Context) args[0];
			targetBookTitle = (String) args[1];
			System.out.println("Target book is " + targetBookTitle);

			// Add a TickerBehaviour that schedules a request to seller agents
			// every minute
			addBehaviour(new TickerBehaviour(this, 3000) {
				protected void onTick() {
					// System.out.println("Trying to buy "+targetBookTitle);
//					System.out.println("find available agents");
					// Update the list of seller agents
					DFAgentDescription template = new DFAgentDescription();
					ServiceDescription sd = new ServiceDescription();
					// sd.setType("book-selling");
					template.addServices(sd);
					try {
						DFAgentDescription[] result = DFService.search(myAgent,
								template);
						serverAgents = result;
						
						notifyAgentsChanged();
//						System.out
//								.println("Found the following seller agents:");
						sellerAgents = new AID[result.length];
						for (int i = 0; i < result.length; ++i) {
							sellerAgents[i] = result[i].getName();
//							System.out
//									.println(sellerAgents[i].getName()
//											+ ", "
//											+ ((ServiceDescription) result[i]
//													.getAllServices().next())
//													.getType());
						}
						//TODO should be here
//						notifyAgentsChanged();
					} catch (FIPAException fe) {
						fe.printStackTrace();
					}

					// Perform the request
					// this.addBehaviour(new GetJarBehaviour());

				}
			});
		} else {
			// Make the agent terminate
			System.out.println("No target book title specified");
			doDelete();
		}
	}

	// Put agent clean-up operations here
	protected void takeDown() {
		// Printout a dismissal message
		System.out.println("Loading-agent " + getAID().getName()
				+ " terminating.");
	}

	/**
	 * public method to get Jar 
	 * 
	 */
	public void runBehaviour(AID serverAgent) {
		this.addBehaviour(new GetJarBehaviour(serverAgent,context));
	}

	public DFAgentDescription[] getAgentsOnServer() {
		return serverAgents;

	}

	private void notifyAgentsChanged() {
		Intent broadcast = new Intent();
		// Bundle bundle = new Bundle();
		// bundle.
		broadcast.setAction("pl.edu.kosttek.REFRESH_AGENTS");
//		Log.i("notify", "Sending broadcast " + broadcast.getAction());
		context.sendBroadcast(broadcast);
	}

	public File getTempFile() {
		return tempFile;
	}

	public void setTempFile(File tempFile) {
		this.tempFile = tempFile;
	}

	
}


