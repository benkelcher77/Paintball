package com.ben.paintball.ecs.components;

import java.awt.event.MouseEvent;

import com.ben.paintball.ecs.Component;
import com.ben.paintball.ecs.GameObject;
import com.ben.paintball.input.Mouse;

public class ButtonController extends Component {
	
	public interface ButtonEvent {
		void onEvent(GameObject source);
	}
	
	private ButtonEvent event;
	private boolean leftclickFlag = false;
	
	public ButtonController(GameObject parent, ButtonEvent event) {
		super(parent);
		
		this.event = event;
	}
	
	@Override
	public boolean update() {
		if (Mouse.instance.isButtonPressed(MouseEvent.BUTTON1) && !leftclickFlag) {
			leftclickFlag = true;
			if (parent.getComponent(BoxBounds.class).getBounds().contains(Mouse.instance.getScreenX(), Mouse.instance.getScreenY())) {
				event.onEvent(parent);
			}
		} else if (!Mouse.instance.isButtonPressed(MouseEvent.BUTTON1) && leftclickFlag)
			leftclickFlag = false;
		
		return false;
	}

}
