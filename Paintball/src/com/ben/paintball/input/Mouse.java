package com.ben.paintball.input;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;

public class Mouse extends MouseAdapter {

	public static Mouse instance = new Mouse();
	
	private int x;
	private int y;
	private int wheelPosition;
	private int prevWheelPosition;
	private boolean[] buttons = new boolean[10];
	
	private List<MouseClickedListener> mouseClickSubscribers = new ArrayList<MouseClickedListener>();
	
	private Mouse() {
		this.x = 0;
		this.y = 0;
		this.wheelPosition = 0;
		this.prevWheelPosition = 0;
		
		for (int i = 0; i < buttons.length; i++)
			buttons[i] = false;
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		x = e.getX();
		y = e.getY();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		x = e.getX();
		y = e.getY();
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		buttons[e.getButton()] = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		buttons[e.getButton()] = false;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		for (MouseClickedListener mcl : mouseClickSubscribers)
			if (mcl.onMouseClicked(e)) 
				break;
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		wheelPosition = e.getWheelRotation();
	}
	
	public void update() {
		prevWheelPosition = wheelPosition;
	}
	
	public int getScreenX() {
		return x;
	}
	
	public int getScreenY() {
		return y;
	}
	
	public boolean isButtonPressed(int button) {
		return buttons[button];
	}
	
	public int getDWheel() {
		return wheelPosition - prevWheelPosition;
	}
	
	public int getWheelPosition() {
		return wheelPosition;
	}
	
	public void mouseClickSubscribe(MouseClickedListener mcl) {
		mouseClickSubscribers.add(mcl);
	}
	
}
