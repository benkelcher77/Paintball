package com.ben.paintball.ecs.components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.ben.paintball.ecs.Component;
import com.ben.paintball.ecs.GameObject;

public class BoxBounds extends Component {
	
	private Transform transform;
	
	private boolean shouldDraw = false;
	private boolean collider = true;
	
	public BoxBounds(GameObject parent) {
		super(parent);
		this.transform = parent.getComponent(Transform.class);
	}
	
	@Override
	public void render(Graphics2D g) {
		if (shouldDraw) {
			g.setColor(Color.WHITE);
			g.draw(getBounds());
			g.draw(getBoundsBottom());
			g.draw(getBoundsTop());
			g.draw(getBoundsLeft());
			g.draw(getBoundsRight());
		}
	}
	
	public Rectangle getBounds() {
		return new Rectangle((int)transform.x, (int)transform.y, transform.sx, transform.sy);
	}
	
	public Rectangle getBoundsBottom() {
		return new Rectangle((int)transform.x + (int)(transform.sx / 4), (int)transform.y + (int)(transform.sy / 2), (int)(transform.sx / 2), (int)(transform.sy / 2));
	}
	
	public Rectangle getBoundsTop() {
		return new Rectangle((int)transform.x + (int)(transform.sx / 4), (int)transform.y, (int)(transform.sx / 2), (int)(transform.sy / 2));
	}
	
	public Rectangle getBoundsLeft() {
		return new Rectangle((int)transform.x, (int)transform.y + (int)(transform.sy / 4), (int)(transform.sx / 4), (int)(transform.sy / 2));
	}
	
	public Rectangle getBoundsRight() {
		return new Rectangle((int)transform.x + (int)(3 * transform.sx / 4), (int)transform.y + (int)(transform.sy / 4), (int)(transform.sx / 4), (int)(transform.sy / 2));
	}
	
	public void shouldDraw(boolean draw) {
		this.shouldDraw = draw;
	}
	
	public void setCollider(boolean collider) {
		this.collider = collider;
	}
	
	public boolean isCollider() {
		return collider;
	}

}
