package com.ben.paintball.ecs.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import com.ben.paintball.ecs.Component;
import com.ben.paintball.ecs.GameObject;
import com.ben.paintball.input.Mouse;

public class ButtonRenderer extends Component {
	
	private BoxBounds bounds;
	
	private String text;
	private Color background;
	private Color foreground;
	private Color highlight;
	private Font font;
	
	public ButtonRenderer(GameObject parent, String text, Color background, Color foreground, Color highlight, Font font) {
		super(parent);
		
		this.bounds = parent.getComponent(BoxBounds.class);
		
		this.text = text;
		this.background = background;
		this.foreground = foreground;
		this.highlight = highlight;
		this.font = font;
	}
	
	@Override
	public void render(Graphics2D g) {
		if (bounds.getBounds().contains(Mouse.instance.getScreenX(), Mouse.instance.getScreenY()))
			g.setColor(highlight);
		else
			g.setColor(background);
		g.fill(bounds.getBounds());
		
		g.setColor(foreground);
		g.draw(bounds.getBounds());
		
		g.setFont(font);
		FontMetrics fm = g.getFontMetrics();
	    
		int drawX = bounds.getBounds().x + ((bounds.getBounds().width - fm.stringWidth(text)) / 2);
	    int drawY = bounds.getBounds().y + ((bounds.getBounds().height - fm.getHeight()) / 2) + fm.getAscent();
	    g.drawString(text, drawX, drawY);
	}

}
