package com.ben.paintball.ecs.components;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Random;

import com.ben.paintball.ecs.Component;
import com.ben.paintball.ecs.GameObject;
import com.ben.paintball.input.Keyboard;
import com.ben.paintball.net.Client;
import com.ben.paintball.net.packets.Packet01Disconnect;
import com.ben.paintball.net.packets.Packet02Move;

public class PlayerController extends Component {
	
	private Transform transform;
	private BoxBounds collider;
	private float moveSpeed;
	private float vx, vy;
	
	private List<GameObject> objects;
	
	private int north = KeyEvent.VK_W;
	private int south = KeyEvent.VK_S;
	private int east  = KeyEvent.VK_D;
	private int west  = KeyEvent.VK_A;
	
	private Random random;
	
	public PlayerController(GameObject parent, float moveSpeed, List<GameObject> objects) {
		super(parent);
		this.transform = parent.getComponent(Transform.class);
		this.collider = parent.getComponent(BoxBounds.class);
		this.moveSpeed = moveSpeed;
		this.vx = 0f;
		this.vy = 0f;
		
		this.objects = objects;
		
		this.random = new Random();
	}
	
	@Override
	public boolean update() {
		this.vx = 0f;
		this.vy = 0f;
		
		if (Keyboard.instance.isKeyDown(north)) {
			vy = -moveSpeed;
		} else if (Keyboard.instance.isKeyDown(south)) {
			vy = moveSpeed;
		}
		
		if (Keyboard.instance.isKeyDown(west)) {
			vx = -moveSpeed;
		} else if (Keyboard.instance.isKeyDown(east)) {
			vx = moveSpeed;
		}
		
		for (GameObject go : objects) {
			if (go.equals(parent) || go.getComponent(PaintballController.class) != null || go.getComponent(GrenadeController.class) != null)
				continue;
			
			if (go.getComponent(BoxBounds.class) != null) {
				BoxBounds bb = go.getComponent(BoxBounds.class);
				Transform other = go.getComponent(Transform.class);
				if (collider.getBoundsLeft().intersects(bb.getBounds())) {
					//System.out.println("Collision On Left");
					vx = 0;
					transform.x = other.x + other.sx;
				} else if (collider.getBoundsRight().intersects(bb.getBounds())) {
					//System.out.println("Collision On Right");
					vx = 0;
					transform.x = other.x - transform.sx;
				}
				
				if (collider.getBoundsTop().intersects(bb.getBounds())) {
					//System.out.println("Collision On Top");
					vy = 0;
					transform.y = other.y + other.sy;
				} else if (collider.getBoundsBottom().intersects(bb.getBounds())) {
					//System.out.println("Collision On Bottom");
					vy = 0;
					transform.y = other.y - transform.sy;
				}
			}
		}
		
		if (parent.getComponent(HitCounter.class) != null) {
			if (parent.getComponent(HitCounter.class).getHitCount() >= 10) {
				if (parent.getComponent(NetClient.class) != null) {
					Packet01Disconnect disconnectPacket = new Packet01Disconnect(parent.getComponent(NetClient.class).getID());
					disconnectPacket.writeData(parent.getComponent(NetClient.class).getClient());
				}
				return true;
			}
		}
		
		transform.x += vx;
		transform.y += vy;
		
		if (parent.getComponent(NetClient.class) != null && (vx != 0 || vy != 0)) { // Don't bother sending movement packets if you didn't move because that's stupid obviously
			Packet02Move movePacket = new Packet02Move(parent.getComponent(NetClient.class).getID(), transform.x, transform.y);
			movePacket.writeData(parent.getComponent(NetClient.class).getClient());
		}
		
		return false;
	}

}
