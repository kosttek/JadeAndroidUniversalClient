package pl.edu.kosttek.jadeclient.agent;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.lang.reflect.InvocationTargetException;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import dalvik.system.DexClassLoader;

public class GetJarBehaviour extends Behaviour {
		private static final String SECONDARY_DEX_NAME = "mydex.jar";
		private MessageTemplate mt; // The template to receive replies
		private int step = 0;
		AID serverAgent;
		byte serializedObject[] = null;
		byte serializedObjectOut[] ;
		Behaviour targetBehaviour;
		private Context context;

		public GetJarBehaviour(AID serverAgent, Context context) {
			this.context = context;
			this.serverAgent = serverAgent;
		}

		private void notifyFileLoaded() {
			Intent broadcast = new Intent();
			broadcast.setAction("pl.edu.kosttek.DYNAMIC_ACTIVITY");
			Log.i("notify", "Sending broadcast " + broadcast.getAction());
			context.sendBroadcast(broadcast);
		}
		
		@Override
		public void action() {
			
			switch (step) {
			case 0:
				ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
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
				block();// wait for message!
				break;
			case 1:
				
				ACLMessage msg = myAgent.receive();

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
					step = 2;
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
				block(); // wait for message!
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
					((AgentBuyerLoader)myAgent).setTempFile(file);
					step = 4;// done
					try {
//						 targetBehaviour = getBehaviour(file, serializedObject);
							notifyFileLoaded();
//						myAgent.addBehaviour(targetBehaviour);

					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();}
//					} catch (InstantiationException e) {
//						e.printStackTrace();
//					} catch (IllegalAccessException e) {
//						e.printStackTrace();
//					} catch (InvocationTargetException e) {
//						e.printStackTrace();
//					} catch (NoSuchMethodException e) {
//						e.printStackTrace();
//					} 
					

				} else {
					System.out.println("jar replay was null sorry ");
					step = 5;//done
					break;
				}
			case 4:
				/**
				 *  serialize				  
				 */
//				ByteArrayOutputStream bos = new ByteArrayOutputStream();
//				ObjectOutput out = null;
//				try {
//					out = new ObjectOutputStream(bos);
//					out.writeObject(targetBehaviour);
//					byte[] yourBytes = bos.toByteArray();
//					serializedObjectOut = yourBytes;
//				} catch (IOException e) {
//					e.printStackTrace();
//				} finally {
//					try {
//						out.close();
//						bos.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//
//				}
//				// serialize
//				
//				request = new ACLMessage(ACLMessage.REQUEST);
//
//				request.addReceiver(serverAgent);
//				request.setConversationId("push_bytes");
//				request.setReplyWith("request" + System.currentTimeMillis()); // Unique
//																	// value
//				request.setByteSequenceContent(serializedObjectOut);
//
//				myAgent.send(request);
//				// Prepare the template to get proposals
				step = 5;//done
				break;

			}
		}

		@Override
		public boolean done() {
			if (step == 5)
				return true;
			return false;
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

