package com.ben.paintball.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import com.ben.paintball.Game;
import com.ben.paintball.ecs.GameObject;
import com.ben.paintball.ecs.components.BoxBounds;
import com.ben.paintball.ecs.components.HitCounter;
import com.ben.paintball.ecs.components.NetID;
import com.ben.paintball.ecs.components.SplatterableSpriteRenderer;
import com.ben.paintball.ecs.components.Transform;
import com.ben.paintball.graphics.SplatterableSprite;
import com.ben.paintball.net.packets.Packet;
import com.ben.paintball.net.packets.Packet.PacketType;
import com.ben.paintball.net.packets.Packet00Login;
import com.ben.paintball.net.packets.Packet01Disconnect;
import com.ben.paintball.net.packets.Packet02Move;
import com.ben.paintball.net.packets.Packet03Shoot;
import com.ben.paintball.net.packets.Packet04Grenade;
import com.ben.paintball.util.Pair;

public class Server extends Thread {

	private Game game;
	
	private DatagramSocket socket;
	private Map<GameObject, Long> connections;
	
	public Server(Game game, int port) {
		this.game = game;
		this.connections = new HashMap<>();
		
		try {
			this.socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		System.out.println("Server created on port " + port);
	}
	
	public void run() {
		while (true) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//System.out.println("CLIENT @ [" + packet.getAddress().getHostAddress() + ":" + packet.getPort() + "] > " + new String(packet.getData()).trim());
			
			parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
		}
	}
	
	private void parsePacket(byte[] data, InetAddress address, int port) {
		String msg = new String(data).trim();
		PacketType type = Packet.lookupType(msg.substring(0, 2));
		
		switch (type) {
		case LOGIN: {
			Packet00Login packet = new Packet00Login(data);
			
			System.out.println("CLIENT @ [" + address.getHostAddress() + ":" + port + "] > Connected");
			
			Pair<InetAddress, Integer> connection = new Pair<>(address, port);
			GameObject netPlayer = new GameObject("Player " + packet.getID());
			netPlayer.addComponent(new Transform(netPlayer, packet.getX(), packet.getY(), 32, 32));
			netPlayer.addComponent(new BoxBounds(netPlayer));
			netPlayer.addComponent(new SplatterableSpriteRenderer(netPlayer, new SplatterableSprite(packet.getColor(), 32, 32, true)));
			netPlayer.addComponent(new NetID(netPlayer, address, port, packet.getID()));
			netPlayer.addComponent(new HitCounter(netPlayer));
			connections.put(netPlayer, packet.getID());
			
			for (GameObject existing : connections.keySet()) {
				if (!connection.equals(existing.getComponent(NetID.class).getConnection())) {
					sendData(packet.getData(), existing.getComponent(NetID.class).getConnection().first, existing.getComponent(NetID.class).getConnection().second);
					sendData(new Packet00Login(connections.get(existing), existing.getComponent(Transform.class).x, existing.getComponent(Transform.class).y, existing.getComponent(SplatterableSpriteRenderer.class).getColor()).getData(), connection.first, connection.second);
				}
			}
			
			break;
		}
		case DISCONNECT: {
			GameObject disconnected = getByConnection(address, port);
			Packet01Disconnect packet = new Packet01Disconnect(connections.get(disconnected));
			
			System.out.println("CLIENT @ [" + address.getHostAddress() + ":" + port + "] > Disconnected");
			
			connections.remove(disconnected);
			packet.writeData(this);
			break;
		}
		case MOVE: {
			Packet02Move packet = new Packet02Move(data);
			Pair<InetAddress, Integer> connection = new Pair<>(address, port);
			
			GameObject src = getByConnection(address, port);
			src.getComponent(Transform.class).x = packet.getX();
			src.getComponent(Transform.class).y = packet.getY();
			
			for (GameObject player : connections.keySet()) {
				if (!connection.equals(player.getComponent(NetID.class).getConnection())) {
					sendData(packet.getData(), player.getComponent(NetID.class).getConnection().first, player.getComponent(NetID.class).getConnection().second);
				}
			}
			break;
		}
		case SHOOT: {
			Packet03Shoot packet = new Packet03Shoot(data);
			Pair<InetAddress, Integer> connection = new Pair<>(address, port);
			
			for (GameObject player : connections.keySet()) {
				if (!connection.equals(player.getComponent(NetID.class).getConnection())) {
					sendData(packet.getData(), player.getComponent(NetID.class).getConnection().first, player.getComponent(NetID.class).getConnection().second);
				}
			}
			break;
		}
		case GRENADE: {
			Packet04Grenade packet = new Packet04Grenade(data);
			Pair<InetAddress, Integer> connection = new Pair<>(address, port);
			
			for (GameObject player : connections.keySet()) {
				if (!connection.equals(player.getComponent(NetID.class).getConnection())) {
					sendData(packet.getData(), player.getComponent(NetID.class).getConnection().first, player.getComponent(NetID.class).getConnection().second);
				}
			}
			break;
		}
		default:
		case INVALID:
			break;
		}
	}
	
	public void sendData(byte[] data, InetAddress ip, int port) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendDataToAllClients(byte[] data) {
		for (GameObject connection : connections.keySet())
			sendData(data, connection.getComponent(NetID.class).getConnection().first, connection.getComponent(NetID.class).getConnection().second);
	}
	
	public GameObject getByConnection(InetAddress address, int port) {
		Pair<InetAddress, Integer> connection = new Pair<>(address, port);
		for (GameObject go : connections.keySet()) {
			if (go.getComponent(NetID.class) != null && go.getComponent(NetID.class).getConnection().equals(connection))
				return go;
		}
		
		return null;
	}
	
}
