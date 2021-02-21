package com.ben.paintball.input;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class Keyboard extends KeyAdapter {
	
	public static Keyboard instance = new Keyboard();

	private List<KeyTypedListener> subscribers = new ArrayList<KeyTypedListener>();
	private boolean[] keys = new boolean[1024];
	
	public Keyboard() {
		for (int i = 0; i < keys.length; i++)
			keys[i] = false;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		for (KeyTypedListener ktl : subscribers)
			ktl.onKeyTyped(e);
	}
	
	public void subscribe(KeyTypedListener ktl) {
		subscribers.add(ktl);
	}
	
	public boolean isKeyDown(int keycode) {
		return keys[keycode];
	}

}
