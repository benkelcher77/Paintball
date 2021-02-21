package com.ben.paintball.net.packets;

import com.ben.paintball.net.Client;
import com.ben.paintball.net.Server;

// Form: [02ID:x,y]

public class Packet02Move extends Packet {

	private long id;
	private float x, y;
	
	public Packet02Move(byte[] data) {
		super(02);
		String msg = readData(data);
		id = Long.parseLong(msg.split(":")[0]);
		x = Float.parseFloat(msg.split(":")[1].split(",")[0]);
		y = Float.parseFloat(msg.split(":")[1].split(",")[1]);
	}
	
	public Packet02Move(long id, float x, float y) {
		super(02);
		this.id = id;
		this.x = x;
		this.y = y;
	}

	@Override
	public void writeData(Client client) {
		client.sendData(getData());
	}

	@Override
	public void writeData(Server server) {
		server.sendDataToAllClients(getData());
	}

	@Override
	public byte[] getData() {
		return ("02" + id + ":" + x + "," + y).getBytes();
	}
	
	public long getID() {
		return id;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}

}
