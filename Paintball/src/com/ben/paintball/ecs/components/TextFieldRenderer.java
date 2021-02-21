package com.ben.paintball.ecs.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import com.ben.paintball.ecs.Component;
import com.ben.paintball.ecs.GameObject;

public class TextFieldRenderer extends Component {

	private BoxBounds bounds;
	private TextFieldController controller;
	
	private Color background;
	private Color foreground;
	private Color defaultTextColor;
	private Font font;
	
	private String defaultText;
	
	private int timer = 0;
	private int timerMax = 30;
	private boolean showPipe = false;
	
	public TextFieldRenderer(GameObject parent, String defaultText, Color background, Color foreground, Color defaultTextColor, Font font) {
		super(parent);
		
		this.bounds = parent.getComponent(BoxBounds.class);
		this.controller = parent.getComponent(TextFieldController.class);
		
		this.defaultText = defaultText;
		this.background = background;
		this.foreground = foreground;
		this.defaultTextColor = defaultTextColor;
		this.font = font;
	}
	
	@Override
	public boolean update() {
		timer++;
		if (timer >= timerMax) {
			timer = 0;
			showPipe = !showPipe;
		}
		
		return false;
	}
	
	@Override
	public void render(Graphics2D g) {
		g.setColor(background);
		g.fill(bounds.getBounds());
		g.setColor(foreground);
		g.draw(bounds.getBounds());
		
		g.setFont(font);
		FontMetrics fm = g.getFontMetrics();	    
	    int drawY = bounds.getBounds().y + ((bounds.getBounds().height - fm.getHeight()) / 2) + fm.getAscent();
	    
		if (controller.getText().equals("") && !controller.isFocused()) {
			g.setColor(defaultTextColor);
			g.drawString(defaultText, bounds.getBounds().x + 2, drawY);
		} else {
			g.setColor(foreground);
			g.drawString(controller.getText() + (showPipe && controller.isFocused() ? "|" : ""), bounds.getBounds().x + 2, drawY);
		}
	}

}
