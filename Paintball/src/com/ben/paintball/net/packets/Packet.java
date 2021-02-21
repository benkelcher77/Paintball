package com.ben.paintball.net.packets;

import com.ben.paintball.net.Client;
import com.ben.paintball.net.Server;

public abstract class Packet {

	public static enum PacketType {
		INVALID(-1),
		LOGIN(00),
		DISCONNECT(01),
		MOVE(02),
		SHOOT(03),
		GRENADE(04);
		
		private int packetID;
		
		private PacketType(int packetID) {
			this.packetID = packetID;
		}
		
		public int getID() {
			return packetID;
		}
	}
	
	public byte packetID;
	
	public Packet(int packetID) {
		this.packetID = (byte)packetID;
	}
	
	public abstract void writeData(Client client);
	public abstract void writeData(Server server);
	public abstract byte[] getData();
	
	public String readData(byte[] data) {
		String message = new String(data).trim();
		return message.substring(2); // the first two characters are the ID of the packet
	}
	
	public static PacketType lookupType(String id) {
		try {
			return lookupType(Integer.parseInt(id));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return PacketType.INVALID;
		}
	}
	
	public static PacketType lookupType(int id) {
		for (PacketType p : PacketType.values())
			if (id == p.getID())
				return p;
		
		return PacketType.INVALID;
	}
}
