package com.ben.paintball.net;

import java.awt.Color;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ConcurrentModificationException;

import com.ben.paintball.Game;
import com.ben.paintball.ecs.GameObject;
import com.ben.paintball.ecs.components.BoxBounds;
import com.ben.paintball.ecs.components.GrenadeController;
import com.ben.paintball.ecs.components.HitCounter;
import com.ben.paintball.ecs.components.NetID;
import com.ben.paintball.ecs.components.PaintballController;
import com.ben.paintball.ecs.components.ResizedBoxBounds;
import com.ben.paintball.ecs.components.SplatterableSpriteRenderer;
import com.ben.paintball.ecs.components.SpriteRenderer;
import com.ben.paintball.ecs.components.Transform;
import com.ben.paintball.game.MultiplayerGameLayer;
import com.ben.paintball.graphics.SplatterableSprite;
import com.ben.paintball.graphics.Sprite;
import com.ben.paintball.net.packets.Packet;
import com.ben.paintball.net.packets.Packet.PacketType;
import com.ben.paintball.net.packets.Packet00Login;
import com.ben.paintball.net.packets.Packet01Disconnect;
import com.ben.paintball.net.packets.Packet02Move;
import com.ben.paintball.net.packets.Packet03Shoot;
import com.ben.paintball.net.packets.Packet04Grenade;
import com.ben.paintball.util.Pair;

public class Client extends Thread {

	private Game game;
	
	private InetAddress ip;
	private int port;
	private DatagramSocket socket;
	
	private long id;
	
	public Client(Game game, String ipAddress, long id, int port) {
		this.game = game;
		this.id = id;
		this.port = port;
		
		try {
			this.socket = new DatagramSocket();
			this.ip = InetAddress.getByName(ipAddress);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
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
			
			parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
			//System.out.println("SERVER > " + new String(packet.getData()));
		}
	}
	
	private void parsePacket(byte[] data, InetAddress address, int port) {
		String msg = new String(data).trim();
		PacketType type = Packet.lookupType(msg.substring(0, 2));
		
		switch (type) {
		case LOGIN: {
			Packet00Login packet = new Packet00Login(data);
			//System.out.println("[" + address.getHostAddress() + ":" + port + "] " + packet.getID() + " has connected");
			
			//System.out.println("Stats: " + packet.getID() + ", " + game + ", " + game.getLayerStack() + ", " + game.getLayerStack().getByType(GameLayer.class));
			if (packet.getID() != id) { // Packet originated from non-local source
				GameObject netPlayer = new GameObject("Player " + packet.getID());
				netPlayer.addComponent(new Transform(netPlayer, packet.getX(), packet.getY(), 32, 32));
				netPlayer.addComponent(new BoxBounds(netPlayer));
				netPlayer.addComponent(new SplatterableSpriteRenderer(netPlayer, new SplatterableSprite(packet.getColor(), 32, 32, true)));
				netPlayer.addComponent(new NetID(netPlayer, address, port, packet.getID()));
				netPlayer.addComponent(new HitCounter(netPlayer));
				
				//System.out.println(game.getLayerStack().peek());
				game.getLayerStack().getByType(MultiplayerGameLayer.class).getHandler().addToQueue(netPlayer);
				
				//System.out.println("Adding net player at " + packet.getX() + ", " + packet.getY() + " with id " + packet.getID());
			} else {
				
			}
			
			break;
		}
		case DISCONNECT: {
			Packet01Disconnect packet = new Packet01Disconnect(data);
			//System.out.println("[" + address.getHostAddress() + ":" + port + "] has disconnected");
			GameObject disconnected = findByNetID(packet.getID());
			if (disconnected != null)
				disconnected.getComponent(NetID.class).disconnect();
			
			break;
		}
		case MOVE: {
			Packet02Move packet = new Packet02Move(data);
			GameObject src = findByNetID(packet.getID());
			if (src != null) {
				src.getComponent(Transform.class).x = packet.getX();
				src.getComponent(Transform.class).y = packet.getY();
			}
			break;
		}
		case SHOOT: {
			Packet03Shoot packet = new Packet03Shoot(data);
			GameObject src = findByNetID(packet.getID());
			if (src != null) {
				GameObject paintball = new GameObject();
				paintball.addComponent(new Transform(paintball, packet.getX(), packet.getY(), 8, 8));
				paintball.addComponent(new BoxBounds(paintball));
				paintball.addComponent(new ResizedBoxBounds(paintball, 128, 128));
				paintball.addComponent(new SpriteRenderer(paintball, new Sprite(src.getComponent(SplatterableSpriteRenderer.class).getColor().darker())));
				paintball.addComponent(new PaintballController(paintball, src, game.getLayerStack().getByType(MultiplayerGameLayer.class).getHandler().getObjects(), packet.getVx(), packet.getVy()));

				game.getLayerStack().getByType(MultiplayerGameLayer.class).getHandler().addToQueue(paintball);
			}
			break;
		}
		case GRENADE: {
			Packet04Grenade packet = new Packet04Grenade(data);
			GameObject src = findByNetID(packet.getID());
			if (src != null) {
				GameObject grenade = new GameObject("Grenade " + System.currentTimeMillis());
				grenade.addComponent(new Transform(grenade, packet.getX(), packet.getY(), 12, 12));
				grenade.addComponent(new BoxBounds(grenade));
				grenade.addComponent(new SpriteRenderer(grenade, new Sprite(src.getComponent(SplatterableSpriteRenderer.class).getColor().darker())));
				grenade.addComponent(new GrenadeController(grenade, src, game.getLayerStack().getByType(MultiplayerGameLayer.class).getHandler(), src.getComponent(SplatterableSpriteRenderer.class).getColor().darker(), packet.getVx(), packet.getVy(), 1.02f, 180));

				game.getLayerStack().getByType(MultiplayerGameLayer.class).getHandler().addToQueue(grenade);
			}
			break;
		}
		default:
		case INVALID:
			break;
		}
	}
	
	public void sendData(byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public GameObject findByNetID(InetAddress address, int port) {
		Pair<InetAddress, Integer> connection = new Pair<>(address, port);
		for (GameObject go : game.getLayerStack().getByType(MultiplayerGameLayer.class).getHandler().getObjects()) {
			if (go.getComponent(NetID.class) != null) {
				if (go.getComponent(NetID.class).getConnection().equals(connection))
					return go;
			}
		}
		
		return null;
	}
	
	public GameObject findByNetID(long id) {
		try {
			synchronized (game.getLayerStack().getByType(MultiplayerGameLayer.class).getHandler().getObjects()) {
				for (GameObject go : game.getLayerStack().getByType(MultiplayerGameLayer.class).getHandler().getObjects()) {
					if (go.getComponent(NetID.class) != null) {
						if (go.getComponent(NetID.class).getID() == id) {
							return go;
						}
					}
				}
			}
		} catch (ConcurrentModificationException e) {
			// stupid fucking concurrent modification exceptions 
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		return super.toString() + ": Client [" + ip.getHostAddress() + "]";
	}

}
