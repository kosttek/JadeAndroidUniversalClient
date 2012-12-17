package pl.edu.kosttek.jadeclient.agent;

import android.content.Context;
import jade.core.Agent;

public class EmptyAgent extends Agent implements AddBehaviourInterface {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Context context;
	private Object fields;
	private OnEmptyAgentLoad onEmptyAgentLoad;

	protected void setup() {
		System.out.println("HalloOo ! empty-agent " + getAID().getName()
				+ " is ready.");
		// TODO it could have to be interface not a class
		registerO2AInterface(AddBehaviourInterface.class, this);
		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			context = (Context) args[0];
			onEmptyAgentLoad = (OnEmptyAgentLoad) args[1];
		}
		if(onEmptyAgentLoad!=null){
			onEmptyAgentLoad.setup(this);
		}
	}

	public Object getFields() {
		return fields;
	}

	public void setFields(Object fields) {
		this.fields = fields;
	}

	public OnEmptyAgentLoad getOnEmptyAgentLoad() {
		return onEmptyAgentLoad;
	}

	public void setOnEmptyAgentLoad(OnEmptyAgentLoad onEmptyAgentLoad) {
		this.onEmptyAgentLoad = onEmptyAgentLoad;
	}

	public interface OnEmptyAgentLoad {
		public void setup(Agent agent);
	}
}
