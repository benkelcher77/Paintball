package com.ben.paintball.net.packets;

import com.ben.paintball.net.Client;
import com.ben.paintball.net.Server;

// Form: [01ID]

public class Packet01Disconnect extends Packet {

	private long id;
	
	public Packet01Disconnect(byte[] data) {
		super(01);
		id = Long.parseLong(readData(data));
		
	}
	
	public Packet01Disconnect(long id) {
		super(01);
		this.id = id;
	}

	public void writeData(Client client) {
		client.sendData(getData());
	}

	public void writeData(Server server) {
		server.sendDataToAllClients(getData());
	}

	public byte[] getData() {
		return ("01" + id).getBytes();
	}
	
	public long getID() {
		return id;
	}

}
