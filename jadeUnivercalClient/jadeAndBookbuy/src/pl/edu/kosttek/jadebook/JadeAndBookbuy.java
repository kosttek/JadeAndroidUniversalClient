package pl.edu.kosttek.jadebook;

import pl.edu.kosttek.jadebook.config.Config;
import android.app.Application;
import android.content.SharedPreferences;

public class JadeAndBookbuy extends Application {
	
	public final static String PREFS_NAME = "jadeprefs";
	SharedPreferences settings;
	
	
	private static final String prefHost = "host";
	private static final String prefPort = "port";
	private static final String prefName = "name";
	
	String startHost = "10.10.0.1";
	String startPort = "1099";
	String startName = "buyer1";
	
	@Override
	public void onCreate() {
		super.onCreate();
		settings = getSharedPreferences(PREFS_NAME, 0);
		getFromPreferences();
	}
	
	public void getFromPreferences(){
		Config.agentName = settings.getString(prefName, startName);
		Config.jadeServerhost = settings.getString(prefHost, startHost);
		Config.jadeServerPort = settings.getString(prefPort, startPort);
	}
	
	public void savePreferences(){
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(prefHost, Config.jadeServerhost);
		editor.putString(prefPort, Config.jadeServerPort);
		editor.putString(prefName, Config.agentName);
		editor.commit();
	}
	
}
