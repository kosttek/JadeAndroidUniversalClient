package pl.edu.kosttek.jadeclient.connection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;

public class ServerSearching {
	static public void findJadeServer(OnServerFoundListener onServerFoundListener){
		MultiCastThread multiCastThread = new MultiCastThread(onServerFoundListener);
		multiCastThread.start();
	}
}

class MultiCastThread extends Thread {
	boolean moreQuotes = true;
	DatagramSocket socket;
	OnServerFoundListener listener;
	public MultiCastThread(OnServerFoundListener listener) {
		this.listener = listener;
	}
	
	public void run() {

			
		
//		while (moreQuotes) {
			try {
				socket = new DatagramSocket(4445);
			
				byte[] buf = new byte[256];
				// don't wait for request...just send a quote

				String dString = null;

				dString = new Date().toString();

				buf = dString.getBytes();

				InetAddress group = InetAddress.getByName("235.1.1.1");
				DatagramPacket packet;
				packet = new DatagramPacket(buf, buf.length, group, 1098);
				socket.send(packet);
//				moreQuotes = false;
				
				byte[] buf2 = new byte[256];
				DatagramPacket receivePacket = new DatagramPacket(buf2, buf2.length);
				socket.receive(receivePacket);
				String data = new String(receivePacket.getData());
				System.out.println("recevied data:"+data);
				listener.found(data);
			} catch (IOException e) {
				e.printStackTrace();
//				moreQuotes = false;
			}
//		}
		socket.close();
	}
}
