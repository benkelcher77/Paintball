package com.ben.paintball.ecs.components;

import java.awt.Rectangle;

import com.ben.paintball.Game;
import com.ben.paintball.ecs.Component;
import com.ben.paintball.ecs.GameObject;

public class CameraFollow extends Component {
	
	private float xOffs, yOffs; // Translate by -xOffs, -yOffs for camera shit
	private Transform target;
	
	public CameraFollow(GameObject parent, Transform target) {
		super(parent);
		this.target = target;
	}
	
	@Override
	public boolean update() {
		xOffs = target.x - (((float)Game.WIDTH / Game.ZOOM) / 2);
		yOffs = target.y - (((float)Game.HEIGHT / Game.ZOOM) / 2);
		
		return false;
	}
	
	public float getXOffs() {
		return xOffs;
	}
	
	public float getYOffs() {
		return yOffs;
	}
	
	public Rectangle getScreenBounds() {
		return new Rectangle((int)xOffs, (int)yOffs, (int)((float)(Game.WIDTH + 32) / Game.ZOOM), (int)((float)(Game.HEIGHT + 32) / Game.ZOOM));
	}

}
