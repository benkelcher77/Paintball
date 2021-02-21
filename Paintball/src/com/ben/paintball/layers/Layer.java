package com.ben.paintball.layers;

import java.awt.Graphics2D;

public abstract class Layer {
	
	private String name;
	
	public Layer(String name) {
		this.name = name;
	}
	
	public void init() {
		return;
	}
	
	public void detach() {
		return;
	}
	
	public void update() {
		return;
	}
	
	public void render(Graphics2D g) {
		return;
	}
	
	public String toString() {
		return "Layer: " + name;
	}

}
