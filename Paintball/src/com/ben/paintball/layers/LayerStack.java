package com.ben.paintball.layers;

import java.awt.Graphics2D;
import java.util.Stack;

public class LayerStack {
	
	private Stack<Layer> layers;
	
	public LayerStack() {
		layers = new Stack<>();
	}
	
	public void update() {
		for (Layer layer : layers)
			layer.update();
	}
	
	public void render(Graphics2D g) {
		for (Layer layer : layers)
			layer.render(g);
	}
	
	public void push(Layer layer) {
		layers.push(layer);
		layer.init();
	}
	
	public Layer pop() {
		Layer layer = layers.pop();
		layer.detach();
		return layer;
	}
	
	public Layer peek() {
		return layers.peek();
	}
	
	public void clear() {
		layers.clear();
	}

	public <T extends Layer> T getByType(Class<T> klass) {
		for (Layer layer : layers)
			if (layer.getClass().equals(klass))
				return klass.cast(layer);
		return null;	
	}
	
}
