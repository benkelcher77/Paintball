package com.ben.paintball.ecs.components;

import com.ben.paintball.ecs.Component;
import com.ben.paintball.ecs.GameObject;
import com.ben.paintball.net.Client;

public class NetClient extends Component {

	private long id;
	private Client client;
	
	public NetClient(GameObject parent, long id, Client client) {
		super(parent);
		this.id = id;
		this.client = client;
	}
	
	public long getID() {
		return id;
	}
	
	public Client getClient() {
		return client;
	}

}
