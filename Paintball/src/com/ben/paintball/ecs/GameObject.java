package com.ben.paintball.ecs;

import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameObject implements Serializable {
	
	private List<Component> components;
	private String name;
	
	public GameObject() {
		this.name = "GameObject " + System.currentTimeMillis();
		this.components = new ArrayList<>();
	}
	
	public GameObject(String name) {
		this.name = name;
		this.components = new ArrayList<>();
	}
	
	public boolean update() {
		boolean result = false;
		
		for (Component c : components)
			if (c.update()) result = true;
		
		return result;
	}
	
	public void render(Graphics2D g) {
		for (Component c : components)
			c.render(g);
	}
	
	public <T extends Component> T getComponent(Class<T> klass) {
		for (Component c : components) {
			if (c.getClass().equals(klass)) {
				return klass.cast(c);
			}
		}
		
		return null;
	}
	
	public <T extends Component> List<T> getComponents(Class<T> klass) {
		List<T> matching = new ArrayList<>();
		for (Component c : components) {
			if (c.getClass().equals(klass)) {
				matching.add(klass.cast(c));
			}
		}
		
		return matching;
	}
	
	public void addComponent(Component component) {
		components.add(component);
	}
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		return "GameObject: " + name;
	}

}
