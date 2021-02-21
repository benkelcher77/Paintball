package com.ben.paintball.net.packets;

import java.awt.Color;

import com.ben.paintball.net.Client;
import com.ben.paintball.net.Server;

// Form: [00ID:x,y,r,g,b]

public class Packet00Login extends Packet {

	private long id;
	private float x, y;
	private int r, g, b;
	
	public Packet00Login(byte[] data) {
		super(00);
		String msg = readData(data);
		this.id = Long.parseLong(msg.split(":")[0]);
		this.x = Float.parseFloat(msg.split(":")[1].split(",")[0]);
		this.y = Float.parseFloat(msg.split(":")[1].split(",")[1]);
		this.r = Integer.parseInt(msg.split(":")[1].split(",")[2]);
		this.g = Integer.parseInt(msg.split(":")[1].split(",")[3]);
		this.b = Integer.parseInt(msg.split(":")[1].split(",")[4]);
	}
	
	public Packet00Login(long id, float x, float y, int r, int g, int b) {
		super(00);
		this.id = id;
		this.x = x;
		this.y = y;
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public Packet00Login(long id, float x, float y, Color color) {
		super(00);
		this.id = id;
		this.x = x;
		this.y = y;
		this.r = color.getRed();
		this.g = color.getGreen();
		this.b = color.getBlue();
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
		return ("00" + id + ":" + x + "," + y + "," + r + "," + g + "," + b).getBytes();
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

	public int getR() {
		return r;
	}

	public int getG() {
		return g;
	}

	public int getB() {
		return b;
	}
	
	public Color getColor() {
		return new Color(r, g, b);
	}

}
