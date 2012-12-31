package pl.edu.kosttek.jadeclient.agent;

import jade.core.AID;
import jade.domain.FIPAAgentManagement.DFAgentDescription;

import java.io.File;

public interface LoaderInterface {
	public  DFAgentDescription[] getAgentsOnServer();
	public void runGetJarBehaviour(AID serverAgent);
	public File getTempFile();
}
