package pl.edu.kosttek.jadebook.agent;

import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;

import java.io.File;

public interface BuyerInterface {
	public  DFAgentDescription[] getAgentsOnServer();
	public void runBehaviour(AID serverAgent);
	public File getTempFile();
}
