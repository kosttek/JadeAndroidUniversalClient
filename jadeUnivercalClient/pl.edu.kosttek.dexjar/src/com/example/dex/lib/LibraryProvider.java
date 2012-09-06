/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.dex.lib;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import android.content.Context;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;



public class LibraryProvider extends Behaviour {
	private AID [] sellerAgents;
	private String targetBookTitle;
	private AID bestSeller; // The agent who provides the best offer 
	private int bestPrice;  // The best offered price
	private int repliesCnt = 0; // The counter of replies from seller agents
	private MessageTemplate mt; // The template to receive replies
	private int step = 0;

	public LibraryProvider(AID [] sellerAgents, String targetBookTitle) {
		this.sellerAgents = sellerAgents;
		this.targetBookTitle = targetBookTitle;
	}
	
	public void action() {
		switch (step) {
		case 0:
			// Send the cfp to all sellers
			ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
			for (int i = 0; i < sellerAgents.length; ++i) {
				cfp.addReceiver(sellerAgents[i]);
			} 
			cfp.setContent(targetBookTitle);
			cfp.setConversationId("book-trade");
			cfp.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value
			myAgent.send(cfp);
			// Prepare the template to get proposals
			mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
					MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
			step = 1;
			break;
		case 1:
			// Receive all proposals/refusals from seller agents
			ACLMessage reply = myAgent.receive(mt);
			if (reply != null) {
				// Reply received
				if (reply.getPerformative() == ACLMessage.PROPOSE) {
					// This is an offer 
					int price = Integer.parseInt(reply.getContent());
					if (bestSeller == null || price < bestPrice) {
						// This is the best offer at present
						bestPrice = price;
						bestSeller = reply.getSender();
					}
				}
				repliesCnt++;
				if (repliesCnt >= sellerAgents.length) {
					// We received all replies
					step = 2; 
				}
			}
			else {
				block();
			}
			break;
		case 2:
			// Send the purchase order to the seller that provided the best offer
			ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
			order.addReceiver(bestSeller);
			order.setContent(targetBookTitle);
			order.setConversationId("book-trade");
			order.setReplyWith("order"+System.currentTimeMillis());
			myAgent.send(order);
			// Prepare the template to get the purchase order reply
			mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
					MessageTemplate.MatchInReplyTo(order.getReplyWith()));
			step = 3;
			break;
		case 3:      
			// Receive the purchase order reply
			reply = myAgent.receive(mt);
			if (reply != null) {
				// Purchase order reply received
				if (reply.getPerformative() == ACLMessage.INFORM) {
					// Purchase successful. We can terminate
					System.out.println(targetBookTitle+" successfully purchased from agent "+reply.getSender().getName());
					System.out.println("Price = "+bestPrice);
					myAgent.doDelete();
				}
				else {
					System.out.println("Attempt failed: requested book already sold.");
				}

				step = 4;
			}
			else {
				block();
			}
			break;
		}        
	}

	public boolean done() {
		if (step == 2 && bestSeller == null) {
			System.out.println("Attempt failed: "+targetBookTitle+" not available for sale");
		}
		return ((step == 2 && bestSeller == null) || step == 4);
	}
}  // End of inner class RequestPerformer
