package pl.edu.kosttek.jadebook.agent;

import jade.core.behaviours.Behaviour;

public interface AddBehaviourInterface {
	public void addBehaviour(Behaviour behaviour);
	public Object getFields();
	public void setFields(Object fields);
}
