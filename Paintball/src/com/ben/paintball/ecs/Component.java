package com.ben.paintball.ecs;

import java.awt.Graphics2D;
import java.io.Serializable;

public abstract class Component implements Serializable {
	
	protected GameObject parent;
	
	public Component(GameObject parent) {
		this.parent = parent;
	}
	
	public boolean update() {
		return false;
	}
	
	public void render(Graphics2D g) {
		return;
	}

}
