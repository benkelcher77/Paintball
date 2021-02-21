package com.ben.paintball.ecs.components;

import java.awt.event.KeyEvent;

import com.ben.paintball.ecs.Component;
import com.ben.paintball.ecs.GameObject;
import com.ben.paintball.input.Keyboard;
import com.ben.paintball.input.Mouse;

public class HotbarController extends Component {

	private int selection = 0;
	
	public HotbarController(GameObject parent) {
		super(parent);
	}
	
	@Override
	public boolean update() {
		if (Keyboard.instance.isKeyDown(KeyEvent.VK_1))  {
			selection = 0;
		} else if (Keyboard.instance.isKeyDown(KeyEvent.VK_2)) {
			selection = 1;
		} else if (Keyboard.instance.isKeyDown(KeyEvent.VK_3)) {
			selection = 2;
		}
		
		// TODO: figure out how the mouse wheel works?
//		if (Mouse.instance.getDWheel() > 0) {
//			selection++;
//			if (selection > 2) 
//				selection = 0;
//		} else if (Mouse.instance.getDWheel() < 0) {
//			selection--;
//			if (selection < 0)
//				selection = 2;
//		}
		
		return false;
	}
	
	public int getSelection() {
		return selection;
	}

}
