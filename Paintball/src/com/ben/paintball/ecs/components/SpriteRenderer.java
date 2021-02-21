package com.ben.paintball.ecs.components;

import java.awt.Graphics2D;

import com.ben.paintball.ecs.Component;
import com.ben.paintball.ecs.GameObject;
import com.ben.paintball.graphics.Sprite;

public class SpriteRenderer extends Component {
	
	private Transform transform;
	private Sprite sprite;
	
	public SpriteRenderer(GameObject parent, Sprite sprite) {
		super(parent);
		this.transform = parent.getComponent(Transform.class);
		this.sprite = sprite;
	}
	
	@Override
	public void render(Graphics2D g) {
		sprite.render(g, transform.x, transform.y, transform.sx, transform.sy);
	}
	
	public Sprite getSprite() {
		return sprite;
	}

}
