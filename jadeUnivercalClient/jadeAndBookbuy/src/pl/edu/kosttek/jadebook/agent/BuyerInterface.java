package pl.edu.kosttek.jadebook.agent;

import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;

public interface BuyerInterface {
	//public  DFAgentDescription[] getServerAgents(); 
	public  DFAgentDescription[] getServerAgents();
	public void getBehaviour(AID serverAgent);
}
