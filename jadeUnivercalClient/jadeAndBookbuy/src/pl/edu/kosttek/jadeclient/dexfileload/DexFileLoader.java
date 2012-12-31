package pl.edu.kosttek.jadeclient.dexfileload;

import jade.android.MicroRuntimeServiceBinder;
import jade.android.RuntimeCallback;
import jade.core.AID;
import jade.wrapper.AgentController;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import android.content.Context;
import dalvik.system.DexClassLoader;

public class DexFileLoader {
	Context context;
	// TODO TEMP
	AID[] sellerAgents;
	String targetBookTitle;

	public DexFileLoader(Context context) {
		this.context = context;
	}

	public void setParams(AID[] sAids, String title) {
		sellerAgents = sAids;
		targetBookTitle = title;
	}

	public Object runInitiator(File internalStoragePath,
			MicroRuntimeServiceBinder mrsb,
			RuntimeCallback<AgentController> rcac)
			throws IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
		final File optimizedDexOutputPath = context.getDir("outdex",
				Context.MODE_PRIVATE);

		DexClassLoader cl = new DexClassLoader(
				internalStoragePath.getAbsolutePath(),
				optimizedDexOutputPath.getAbsolutePath(), null,
				context.getClassLoader());
		Class myClass = null;

//		try {
		//TODO silient crash in here
			myClass = cl.loadClass("com.example.dex.lib.Initiator");

//		} catch (Exception exception) {
//
//			exception.printStackTrace();
//			System.out.println(exception.getMessage());
//		}

		Object result = null;

		Object object = myClass.getConstructor().newInstance();

		// MicroRuntimeServiceBinder mrsb, RuntimeCallback<AgentController> rcac
		result = myClass.getMethod("init", MicroRuntimeServiceBinder.class,
				RuntimeCallback.class, Context.class).invoke(object, mrsb,
				rcac, context);

		return result;
	}



}