package com.ben.paintball.ecs.components;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.ben.paintball.ecs.Component;
import com.ben.paintball.ecs.GameObject;
import com.ben.paintball.input.Keyboard;
import com.ben.paintball.input.Mouse;

public class TextFieldController extends Component {

	private Transform transform;
	private BoxBounds bounds;
	
	private String text;
	
	private boolean focused = false;
	private boolean leftclickFlag = false;
	
	public TextFieldController(GameObject parent) {
		super(parent);
		
		this.transform = parent.getComponent(Transform.class);
		this.bounds = parent.getComponent(BoxBounds.class);
		
		this.text = "";
		
		Keyboard.instance.subscribe((e) -> {
			if (isFocused()) {
				char c = e.getKeyChar();
				if(c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) { // assume backspace 
					text = text.substring(0, text.length() - 1);
				} else {
					text = text.concat(new String(new char[] { c }));
				}
			}
		});
	}
	
	@Override
	public boolean update() {
		if (Mouse.instance.isButtonPressed(MouseEvent.BUTTON1) && !leftclickFlag) {
			leftclickFlag = true;
			if (parent.getComponent(BoxBounds.class).getBounds().contains(Mouse.instance.getScreenX(), Mouse.instance.getScreenY())) {
				focused = true;
			} else {
				focused = false;
			}
		} else if (!Mouse.instance.isButtonPressed(MouseEvent.BUTTON1) && leftclickFlag)
			leftclickFlag = false;
		
		return false;
	}
	
	public boolean isFocused() {
		return focused;
	}

	public String getText() { 
		return text;
	}
	
}
