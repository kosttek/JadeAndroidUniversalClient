package pl.edu.kosttek.jadebook.connection;

import jade.android.AndroidHelper;
import jade.android.MicroRuntimeService;
import jade.android.MicroRuntimeServiceBinder;
import jade.android.RuntimeCallback;
import jade.core.MicroRuntime;
import jade.core.Profile;
import jade.util.Logger;
import jade.util.leap.Properties;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;

import java.util.logging.Level;

import pl.edu.kosttek.jadebook.agent.AgentBuyerLoader;
import pl.edu.kosttek.jadebook.agent.BookBuyerAgent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class ServerConnection {
	public static String AGENT_NAME = "buyer1";
	private Logger logger = Logger.getJADELogger(this.getClass().getName());

	private MicroRuntimeServiceBinder microRuntimeServiceBinder;
	private ServiceConnection serviceConnection;

	private Context context;
	private RuntimeCallback<AgentController> agentStartupCallback = new RuntimeCallback<AgentController>() {
		@Override
		public void onSuccess(AgentController agent) {
		}

		@Override
		public void onFailure(Throwable throwable) {
			logger.log(Level.INFO, "Nickname already in use!");

		}
	};

	public ServerConnection(Context context) {
		this.context = context;
	}

	public void startConnection(final String host, final String port) {
		startConnection(host, port, agentStartupCallback);
	}

	private void startConnection(	final String host, 
									final String port,
									final RuntimeCallback<AgentController> agentStartupCallback) {

		final Properties profile = new Properties();
		setProperties(host, port, profile);

		if (microRuntimeServiceBinder == null) {
			serviceConnection = new ServiceConnection() {
				public void onServiceConnected(ComponentName className,
						IBinder service) {
					microRuntimeServiceBinder = (MicroRuntimeServiceBinder) service;
					logger.log(Level.INFO,
							"Gateway successfully bound to MicroRuntimeService");
					startContainer(profile, agentStartupCallback);
				};

				public void onServiceDisconnected(ComponentName className) {
					microRuntimeServiceBinder = null;
					logger.log(Level.INFO,
							"Gateway unbound from MicroRuntimeService");
				}
			};
			logger.log(Level.INFO, "Binding Gateway to MicroRuntimeService...");
			context.bindService(new Intent(context.getApplicationContext(),
					MicroRuntimeService.class), serviceConnection,
					Context.BIND_AUTO_CREATE);
		} else {
			logger.log(Level.INFO,
					"MicroRumtimeGateway already binded to service");
			startContainer(profile, agentStartupCallback);
		}
	}

	private void setProperties(final String host, final String port,
			final Properties profile) {
		profile.setProperty(Profile.MAIN_HOST, host);
		profile.setProperty(Profile.MAIN_PORT, port);
		profile.setProperty(Profile.MAIN, Boolean.FALSE.toString());
		profile.setProperty(Profile.JVM, Profile.ANDROID);

		setLocalHost(profile);
		profile.setProperty(Profile.LOCAL_PORT, "2000"); // Emulator: this is
															// not really needed
															// on a real device
	}

	private void setLocalHost(final Properties profile) {
		if (AndroidHelper.isEmulator()) {
			// Emulator: this is needed to work with emulated devices
			profile.setProperty(Profile.LOCAL_HOST, AndroidHelper.LOOPBACK);
		} else {
			profile.setProperty(Profile.LOCAL_HOST,
					AndroidHelper.getLocalIPAddress());
		}
	}

	private void startContainer(Properties profile,
			final RuntimeCallback<AgentController> agentStartupCallback) {
		if (!MicroRuntime.isRunning()) {
			microRuntimeServiceBinder.startAgentContainer(profile,
					new RuntimeCallback<Void>() {
						@Override
						public void onSuccess(Void thisIsNull) {
							logger.log(Level.INFO,
									"Successfully start of the container...");

							startAgent(AGENT_NAME, agentStartupCallback);
						}

						@Override
						public void onFailure(Throwable throwable) {
							logger.log(Level.SEVERE,
									"Failed to start the container...");
						}
					});
		} else {

			startAgent(AGENT_NAME, agentStartupCallback);

		}
	}

	private void startAgent(final String nickname,
			final RuntimeCallback<AgentController> agentStartupCallback) {

		if (microRuntimeServiceBinder.pingBinder())
			microRuntimeServiceBinder.startAgent(nickname,
					AgentBuyerLoader.class.getName(), new Object[] { context,
							"book" }, new RuntimeCallback<Void>() {
						@Override
						public void onSuccess(Void thisIsNull) {
							logger.log(Level.INFO, "Successfully start of the "
									+AgentBuyerLoader.class.getName() + "...");
							try {
								agentStartupCallback.onSuccess(MicroRuntime
										.getAgent(nickname));
							} catch (ControllerException e) {
								// Should never happen
								agentStartupCallback.onFailure(e);
							}
						}

						@Override
						public void onFailure(Throwable throwable) {
							logger.log(Level.SEVERE, "Failed to start the "
									+ AgentBuyerLoader.class.getName() + "...");
							agentStartupCallback.onFailure(throwable);
						}
					});

	}
}
