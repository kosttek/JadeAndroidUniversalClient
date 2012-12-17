package pl.edu.kosttek.jadeclient.dexfileload;

import jade.android.MicroRuntimeServiceBinder;
import jade.android.RuntimeCallback;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.wrapper.AgentController;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;
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
		//TODO  crash in here
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

	public Behaviour getBehaviour(File internalStoragePath,
			byte[] serializedObject) throws IllegalArgumentException,
			SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		final File optimizedDexOutputPath = context.getDir("outdex",
				Context.MODE_PRIVATE);

		DexClassLoader cl = new DexClassLoader(
				internalStoragePath.getAbsolutePath(),
				optimizedDexOutputPath.getAbsolutePath(), null,
				context.getClassLoader());
		Class myClass = null;
		// =========list classes in jar

		// String path = internalStoragePath.getAbsolutePath();
		// try {
		// DexFile dx = DexFile.loadDex(path, File.createTempFile("opt",
		// "dex",
		// context.getCacheDir()).getPath(), 0);
		// // Print all classes in the DexFile
		// System.out.println("SYSO");
		// Log.i("","log");
		// for(Enumeration<String> classNames = dx.entries();
		// classNames.hasMoreElements();) {
		// String className = classNames.nextElement();
		// System.out.println("class: " + className);
		// }
		// } catch (IOException e) {
		// Log.w("error", "Error opening " + path, e);
		// }
		// =========
		try {

			myClass = cl.loadClass("com.example.dex.lib.LibraryProvider");

		} catch (Exception exception) {

			exception.printStackTrace();
		}
		Object o = null;
		if (serializedObject != null && serializedObject.length > 5) {
			ByteArrayInputStream bis = new ByteArrayInputStream(
					serializedObject);
			ObjectInput in = null;
			try {
				in = new MyObjectInputStream(bis, cl);
				o = in.readObject();
				System.out.println(o);
			} catch (StreamCorruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					if (bis != null)
						bis.close();
					if (in != null)
						in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
		// TEMPORARY
		// TODO
		;

		Behaviour result = null;
		if (o == null) {
			result = (Behaviour) myClass.getConstructor(AID[].class,
					String.class, Context.class).newInstance(sellerAgents,
					targetBookTitle, context);

		} else {
			myClass.getMethod("setParams", AID[].class, String.class,
					Context.class).invoke(o, sellerAgents, targetBookTitle,
					context);
			myClass.getMethod("incrementNumber").invoke(o, null);

			result = (Behaviour) o;
		}
		return result;
	}
}

// MYCLASSES

class MyObjectInputStream extends ObjectInputStream {
	ClassLoader classLoader;

	public MyObjectInputStream(InputStream input,
			ClassLoader additionalClassLoader) throws StreamCorruptedException,
			IOException {
		super(input);
		this.classLoader = additionalClassLoader;
	}

	@Override
	protected Class resolveClass(ObjectStreamClass desc) throws IOException,
			ClassNotFoundException {
		String name = desc.getName();
		try {
			return Class.forName(name, false, classLoader);
		} catch (ClassNotFoundException ex) {
			// Class cl = (Class) primClasses.get(name);
			// if (cl != null) {
			// return cl;
			// } else {
			// throw ex;
			// }
			throw ex;
		}
	}

}