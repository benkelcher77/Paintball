package com.ben.paintball.ecs.components;

import java.awt.Rectangle;

import com.ben.paintball.ecs.Component;
import com.ben.paintball.ecs.GameObject;

public class ResizedBoxBounds extends Component {

	private Transform transform;
	private int sx, sy;
	
	public ResizedBoxBounds(GameObject parent, int sx, int sy) {
		super(parent);
		
		this.transform = parent.getComponent(Transform.class);
		this.sx = sx;
		this.sy = sy;
	}
	
	public Rectangle getBounds() {
		return new Rectangle((int)(transform.x + (transform.sx / 2) - (sx / 2)), (int)(transform.y + (transform.sy / 2) - (sy / 2)), sx, sy);
	}

}
