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

package pl.edu.kosttek.jadebook.agent;

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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import dalvik.system.DexClassLoader;

public class AgentBuyerLoader extends Agent implements BuyerInterface {
	private static final String SECONDARY_DEX_NAME = "mydex.jar";
	// The title of the book to buy
	private String targetBookTitle;
	// The list of known seller agents
	private AID[] sellerAgents;
	private DFAgentDescription[] serverAgents;
	private Context context;

	// Put agent initializations here
	protected void setup() {
		// Printout a welcome message
		System.out.println("Hallo! Buyer-agent "+getAID().getName()+" is ready.");
		registerO2AInterface(BuyerInterface.class, this);
		// Get the title of the book to buy as a start-up argument
		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			context = (Context) args[0];
			targetBookTitle = (String) args[1];
			System.out.println("Target book is "+targetBookTitle);

			// Add a TickerBehaviour that schedules a request to seller agents every minute
			addBehaviour(new TickerBehaviour(this, 6000) {
				protected void onTick() {
					System.out.println("Trying to buy "+targetBookTitle);
					// Update the list of seller agents
					DFAgentDescription template = new DFAgentDescription();
					ServiceDescription sd = new ServiceDescription();
					//sd.setType("book-selling");
					template.addServices(sd);
					try {
						DFAgentDescription[] result = DFService.search(myAgent, template); 
						serverAgents = result;
						notifyAgentsChanged();
						
						System.out.println("Found the following seller agents:");
						sellerAgents = new AID[result.length];
						for (int i = 0; i < result.length; ++i) {
							sellerAgents[i] = result[i].getName();
							System.out.println(sellerAgents[i].getName()+", "+((ServiceDescription)result[i].getAllServices().next()).getType());
						}
					}
					catch (FIPAException fe) {
						fe.printStackTrace();
					}

					// Perform the request
//					this.addBehaviour(new GetJarBehaviour());
					
				}
			} );
		}
		else {
			// Make the agent terminate
			System.out.println("No target book title specified");
			doDelete();
		}
	}

	// Put agent clean-up operations here
	protected void takeDown() {
		// Printout a dismissal message
		System.out.println("Buyer-agent "+getAID().getName()+" terminating.");
	}

	/**
	   Inner class RequestPerformer.
	   This is the behaviour used by Book-buyer agents to request seller 
	   agents the target book.
	 */
	public void getBehaviour(AID serverAgent){
		this.addBehaviour(new GetJarBehaviour(serverAgent));
	}

	public DFAgentDescription[] getServerAgents() {
		// TODO Auto-generated method stub
		return serverAgents;
		
	}

	private void notifyAgentsChanged() {
		Intent broadcast = new Intent();
//		Bundle bundle = new Bundle();
//		bundle.
		broadcast.setAction("pl.edu.kosttek.REFRESH_AGENTS");
		Log.i("notify", "Sending broadcast " + broadcast.getAction());
		context.sendBroadcast(broadcast);
	}
	
	public class GetJarBehaviour extends Behaviour{
		private MessageTemplate mt; // The template to receive replies
		private int step = 0;
		AID serverAgent;
		
		public GetJarBehaviour(AID serverAgent) {
			// TODO Auto-generated constructor stub
			this.serverAgent = serverAgent;
		}
		@Override
		public void action() {
			switch (step) {
			case 0:
				// Send the cfp to all sellers
				ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
//				for (int i = 0; i < sellerAgents.length; ++i) {
//					request.addReceiver(sellerAgents[i]);
//				} 
				request.addReceiver(serverAgent);
				request.setConversationId("get-jar");
				request.setReplyWith("request"+System.currentTimeMillis()); // Unique value
				myAgent.send(request);
				// Prepare the template to get proposals
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("get-jar"),
						MessageTemplate.MatchInReplyTo(request.getReplyWith()));
				step = 1;
				break;
			case 1:
				ACLMessage reply = myAgent.receive(mt);
				if (reply != null) {
					byte [] bytes =  reply.getByteSequenceContent();
					System.out.println("+++++ "+bytes.length);
					
					File directory = Environment.getExternalStorageDirectory();
	                File file = new File(directory + "/"+SECONDARY_DEX_NAME);
	                prepareDex(bytes, file);
	                try {
						myAgent.addBehaviour(getBehaviour(file));
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	                
				}
			}
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
		
		public Behaviour getBehaviour(File internalStoragePath) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
            final File optimizedDexOutputPath = context.getDir("outdex", Context.MODE_PRIVATE);

           
            
            DexClassLoader cl = new DexClassLoader(internalStoragePath.getAbsolutePath(),
                    optimizedDexOutputPath.getAbsolutePath(),
                    null,
                    context.getClassLoader());
            Class myClass = null;
            //=========list classes in jar
            
//            String path = internalStoragePath.getAbsolutePath();
//            		try {
//            		    DexFile dx = DexFile.loadDex(path, File.createTempFile("opt", "dex",
//            		    		context.getCacheDir()).getPath(), 0);
//            		    // Print all classes in the DexFile
//            		    System.out.println("SYSO");
//            		    Log.i("","log");
//            		    for(Enumeration<String> classNames = dx.entries(); classNames.hasMoreElements();) {
//            		        String className = classNames.nextElement();
//            		        System.out.println("class: " + className);
//            		    }
//            		} catch (IOException e) {
//            		    Log.w("error", "Error opening " + path, e);
//            		}
            //=========
            try {

                myClass =
                        cl.loadClass("com.example.dex.lib.LibraryProvider");
   
            } catch (Exception exception) {
 
                exception.printStackTrace();
            }
			return (Behaviour) myClass.getConstructor(AID [].class , String.class ).newInstance(sellerAgents, targetBookTitle);
        }
    }
    private boolean prepareDex(byte [] code,File dexInternalStoragePath) {

        OutputStream dexWriter = null;

        try {
            
            dexWriter = new BufferedOutputStream(new FileOutputStream(dexInternalStoragePath));
            
            dexWriter.write(code);
            
            dexWriter.close();

            return true;
        } catch (IOException e) {
            if (dexWriter != null) {
                try {
                    dexWriter.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }

            return false;
        }
    }
	
}
