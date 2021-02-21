package com.ben.paintball.net.packets;

import com.ben.paintball.net.Client;
import com.ben.paintball.net.Server;

public class Packet04Grenade extends Packet {

	private long id;
	private float x, y;
	private float vx, vy;

	public Packet04Grenade(byte[] data) {
		super(04);
		
		String msg = readData(data);
		System.out.println(new String(data));
		id = Long.parseLong(msg.split(":")[0]);
		x  = Float.parseFloat(msg.split(":")[1].split(",")[0]);
		y  = Float.parseFloat(msg.split(":")[1].split(",")[1]);
		vx = Float.parseFloat(msg.split(":")[1].split(",")[2]);
		vy = Float.parseFloat(msg.split(":")[1].split(",")[3]);
	}

	public Packet04Grenade(long id, float x, float y, float vx, float vy) {
		super(04);
		this.id = id;
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
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
		return ("04" + id + ":" + x + "," + y + "," + vx + "," + vy).getBytes();
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

	public float getVx() {
		return vx;
	}

	public float getVy() {
		return vy;
	}

}
