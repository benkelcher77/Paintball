package com.ben.paintball.ecs.components;

import com.ben.paintball.ecs.Component;
import com.ben.paintball.ecs.GameObject;

public class Transform extends Component {
	
	public float x, y;
	public int sx, sy;
	
	public Transform(GameObject parent, float x, float y, int sx, int sy) {
		super(parent);
		this.x = x;
		this.y = y;
		this.sx = sx;
		this.sy = sy;
	}
	
	@Override
	public String toString() {
		return "Transform[" + x + ", " + y + ", " + sx + ", " + sy + "]";
	}

}
