package com.ben.paintball.ecs.components;

import java.net.InetAddress;

import com.ben.paintball.ecs.Component;
import com.ben.paintball.ecs.GameObject;
import com.ben.paintball.util.Pair;

public class NetID extends Component {

	private Pair<InetAddress, Integer> connection;
	private long id;
	
	private boolean shouldDisconnect = false;
	
	public NetID(GameObject parent, Pair<InetAddress, Integer> connection, long id) {
		super(parent);
		this.connection = connection;
		this.id = id;
	}
	
	public NetID(GameObject parent, InetAddress address, int port, long id) {
		super(parent);
		this.connection = new Pair<>(address, port);
		this.id = id;
	}
	
	@Override
	public boolean update() {
		return shouldDisconnect;
	}
	
	public void disconnect() {
		shouldDisconnect = true;
	}
	
	public Pair<InetAddress, Integer> getConnection() {
		return connection;
	}
	
	public long getID() {
		return id;
	}

}
