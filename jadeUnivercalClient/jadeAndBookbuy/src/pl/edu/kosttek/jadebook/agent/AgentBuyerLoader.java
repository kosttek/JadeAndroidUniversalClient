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

import android.content.Context;
import android.content.Intent;
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
					System.out.println("find available agents");
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

						System.out
								.println("Found the following seller agents:");
						sellerAgents = new AID[result.length];
						for (int i = 0; i < result.length; ++i) {
							sellerAgents[i] = result[i].getName();
							System.out
									.println(sellerAgents[i].getName()
											+ ", "
											+ ((ServiceDescription) result[i]
													.getAllServices().next())
													.getType());
						}
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
		System.out.println("Buyer-agent " + getAID().getName()
				+ " terminating.");
	}

	/**
	 * Inner class RequestPerformer. This is the behaviour used by Book-buyer
	 * agents to request seller agents the target book.
	 */
	public void getBehaviour(AID serverAgent) {
		this.addBehaviour(new GetJarBehaviour(serverAgent));
	}

	public DFAgentDescription[] getServerAgents() {
		// TODO Auto-generated method stub
		return serverAgents;

	}

	private void notifyAgentsChanged() {
		Intent broadcast = new Intent();
		// Bundle bundle = new Bundle();
		// bundle.
		broadcast.setAction("pl.edu.kosttek.REFRESH_AGENTS");
		Log.i("notify", "Sending broadcast " + broadcast.getAction());
		context.sendBroadcast(broadcast);
	}

	public class GetJarBehaviour extends Behaviour {
		private MessageTemplate mt; // The template to receive replies
		private int step = 0;
		AID serverAgent;
		byte serializedObject[] = null;
		byte serializedObjectOut[] ;
		Behaviour targetBehaviour;

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
				// for (int i = 0; i < sellerAgents.length; ++i) {
				// request.addReceiver(sellerAgents[i]);
				// }
				request.addReceiver(serverAgent);
				request.setConversationId("get-jar");
				request.setReplyWith("request" + System.currentTimeMillis()); // Unique
																				// value
				request.setContent("first");
				myAgent.send(request);
				// Prepare the template to get proposals
				mt = MessageTemplate.and(
						MessageTemplate.MatchConversationId("get-jar"),
						MessageTemplate.MatchInReplyTo(request.getReplyWith()));
				step = 1;
				break;
			case 1:

				ACLMessage msg = myAgent.receive(mt);

				if (msg != null) {
					byte[] bytes = msg.getByteSequenceContent();
					if (bytes == null || bytes.length == 0) {
						System.out.println("empty response object");
					} else {
						System.out.println(bytes.length
								+ "bytes length response object");
					}
					serializedObject = bytes;
					step = 2;

				} else {
					System.out.println("object replay was null sorry ");
					break;
				}
				break;
			// break; ?? or not break
			case 2:

				request = new ACLMessage(ACLMessage.REQUEST);

				request.addReceiver(serverAgent);
				request.setConversationId("get-jar");
				request.setReplyWith("request" + System.currentTimeMillis()); // Unique
																				// value
				request.setContent("secound");
				myAgent.send(request);
				// Prepare the template to get proposals
				mt = MessageTemplate.and(
						MessageTemplate.MatchConversationId("get-jar"),
						MessageTemplate.MatchInReplyTo(request.getReplyWith()));
				step = 3;
				break;
			case 3:

				msg = myAgent.receive(mt);

				if (msg != null) {
					byte[] bytes = msg.getByteSequenceContent();
					if (bytes == null || bytes.length == 0)
						System.out.println("empty response");
					System.out.println("+++++ " + bytes.length);

					File directory = Environment.getExternalStorageDirectory();
					File file = new File(directory + "/" + SECONDARY_DEX_NAME);
					prepareDex(bytes, file);
					step = 4;// done
					try {
						 targetBehaviour = getBehaviour(file, serializedObject);

						myAgent.addBehaviour(targetBehaviour);
						
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
					

				} else {
					System.out.println("jar replay was null sorry ");
					block();
					break;
				}
			case 4:
				// serialize
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutput out = null;
				try {
					out = new ObjectOutputStream(bos);
					out.writeObject(targetBehaviour);
					byte[] yourBytes = bos.toByteArray();
					serializedObjectOut = yourBytes;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						out.close();
						bos.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				// serialize
				
				request = new ACLMessage(ACLMessage.REQUEST);

				request.addReceiver(serverAgent);
				request.setConversationId("push_bytes");
				request.setReplyWith("request" + System.currentTimeMillis()); // Unique
																	// value
				request.setByteSequenceContent(serializedObjectOut);

				myAgent.send(request);
				// Prepare the template to get proposals
				mt = MessageTemplate.and(
						MessageTemplate.MatchConversationId("get-jar"),
						MessageTemplate.MatchInReplyTo(request.getReplyWith()));
				step = 5;
				break;

			}
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			if (step == 5)
				return true;
			return false;
		}

		public Behaviour getBehaviour(File internalStoragePath,
				byte[] serializedObject) throws IllegalArgumentException,
				SecurityException, InstantiationException,
				IllegalAccessException, InvocationTargetException,
				NoSuchMethodException {
			final File optimizedDexOutputPath = context.getDir("outdex",
					Context.MODE_PRIVATE);

			DexClassLoader cl = new DexClassLoader(
					internalStoragePath.getAbsolutePath(),
					optimizedDexOutputPath.getAbsolutePath(), null,
					context.getClassLoader());
			Class myClass = null;
			// =========list classes in jar

			// String path = internalStoragePath.getAbsolutePath();
			// try {
			// DexFile dx = DexFile.loadDex(path, File.createTempFile("opt",
			// "dex",
			// context.getCacheDir()).getPath(), 0);
			// // Print all classes in the DexFile
			// System.out.println("SYSO");
			// Log.i("","log");
			// for(Enumeration<String> classNames = dx.entries();
			// classNames.hasMoreElements();) {
			// String className = classNames.nextElement();
			// System.out.println("class: " + className);
			// }
			// } catch (IOException e) {
			// Log.w("error", "Error opening " + path, e);
			// }
			// =========
			try {

				myClass = cl.loadClass("com.example.dex.lib.LibraryProvider");

			} catch (Exception exception) {

				exception.printStackTrace();
			}
			Object o= null;
			if (serializedObject != null && serializedObject.length > 5) {
				ByteArrayInputStream bis = new ByteArrayInputStream(
						serializedObject);
				ObjectInput in = null;
				try {
					in = new MyObjectInputStream(bis,cl);
					o = in.readObject();
					System.out.println(o);
				} catch (StreamCorruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						if (bis != null)
							bis.close();
						if (in != null)
							in.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
			Behaviour result = null;
			if(o == null){
				result = (Behaviour) myClass.getConstructor(AID[].class,
					String.class).newInstance(sellerAgents, targetBookTitle);
				
			}
			else{
				myClass.getMethod("setParams", AID[].class,
					String.class).invoke(o, sellerAgents, targetBookTitle);
				myClass.getMethod("incrementNumber").invoke(o, null);
				
				result = (Behaviour) o;
			}
			return result;
		}
	}

	private boolean prepareDex(byte[] code, File dexInternalStoragePath) {

		OutputStream dexWriter = null;

		try {

			dexWriter = new BufferedOutputStream(new FileOutputStream(
					dexInternalStoragePath));

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

// MYCLASSES
/*
 * 
 * 
 * 
 * 
 */

class MyObjectInputStream extends ObjectInputStream {
	ClassLoader classLoader;

	public MyObjectInputStream(InputStream input, ClassLoader additionalClassLoader)
			throws StreamCorruptedException, IOException {
		super(input);
		this.classLoader = additionalClassLoader;
	}

	@Override
	protected Class resolveClass(ObjectStreamClass desc) throws IOException,
			ClassNotFoundException {
		String name = desc.getName();
		try {
			return Class.forName(name, false, classLoader);
		} catch (ClassNotFoundException ex) {
//			Class cl = (Class) primClasses.get(name);
//			if (cl != null) {
//				return cl;
//			} else {
//				throw ex;
//			}
			throw ex;
		}
	}

}
