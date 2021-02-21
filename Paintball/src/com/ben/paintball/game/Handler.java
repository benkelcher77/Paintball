package com.ben.paintball.game;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.ben.paintball.ecs.GameObject;
import com.ben.paintball.ecs.components.CameraFollow;
import com.ben.paintball.ecs.components.Transform;

public class Handler {
	
	private List<GameObject> objects = Collections.synchronizedList(new ArrayList<GameObject>());
	private List<GameObject> queue = Collections.synchronizedList(new ArrayList<GameObject>());
	
	public Handler() { }
	
	public void update() {
		Iterator<GameObject> it = objects.iterator();
		while (it.hasNext()) {
			GameObject go = it.next();
			if (go.update())
				it.remove();
		}
		
		for (GameObject go : queue)
			objects.add(go);
		
		queue.clear();
	}
	
	public void render(Graphics2D g, GameObject camera) {
		CameraFollow cam = camera.getComponent(CameraFollow.class);
		for (GameObject go : objects) {
			Transform t = go.getComponent(Transform.class);
			if (t == null)
				continue;
			
			Rectangle renderBounds = new Rectangle((int)t.x, (int)t.y, t.sx, t.sy);
			if (renderBounds.intersects(cam.getScreenBounds()))
				go.render(g);
		}
	}
	
	public void addObject(GameObject... objs) {
		for (GameObject go : objs)
			objects.add(go);
	}
	
	public void removeObject(GameObject... objs) {
		for (GameObject go : objs)
			objects.remove(go);
	}
	
	public synchronized List<GameObject> getObjects() {
		return objects;
	}
	
	public void addToQueue(GameObject... objs) {
		for (GameObject go : objs)
			queue.add(go);
	}
	
	public GameObject getByName(String name) {
		for (GameObject go : objects)
			if (go.getName().equals(name))
				return go;
		
		return null;
	}

}
