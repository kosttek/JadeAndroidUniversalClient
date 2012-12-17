package pl.edu.kosttek.jadeservice;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;

public class BroadcastReceiver extends Agent {
	MulticastSocket socket;
	InetAddress group;
	int jadePort=1099;

	@Override
	protected void setup() {
		super.setup();
		try {
			socket = new MulticastSocket(1098);

			group = InetAddress.getByName("235.1.1.1");
			socket.joinGroup(group);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		addBehaviour(new Receive());
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		socket.leaveGroup(group);
		socket.close();
	}
	
	class Receive extends CyclicBehaviour {

		@Override
		public void action() {
			try {

				DatagramPacket packet;

				byte[] buf = new byte[256];
				packet = new DatagramPacket(buf, buf.length);

				socket.receive(packet);

				String received = new String(packet.getData());
				System.out.println("Quote of the Moment: " + received
						+ "\naddress was " + packet.getAddress());

				byte[] buf2 = new byte[256];
				buf2 = new String(getWlan0Address() + ":"+jadePort).getBytes();
				DatagramPacket packetSend = new DatagramPacket(buf2,
						buf2.length, packet.getAddress(), packet.getPort());
				socket.send(packetSend);

				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

		private InetAddress getWlan0Address() throws SocketException {
			InetAddress result = null;
			NetworkInterface ni = NetworkInterface.getByName("wlan0");
			for (InetAddress ia : Collections.list(ni.getInetAddresses())) {
				if (ia instanceof Inet4Address) {
					result = ia;
					break;
				}
			}
			return result;
		}

	}
}
