package com.ben.paintball.ecs.components;

import java.awt.Color;
import java.awt.Graphics2D;

import com.ben.paintball.ecs.Component;
import com.ben.paintball.ecs.GameObject;
import com.ben.paintball.graphics.SplatterableSprite;
import com.ben.paintball.graphics.Sprite;

public class SplatterableSpriteRenderer extends Component {
	
	private Transform transform;
	private SplatterableSprite sprite;
	
	public SplatterableSpriteRenderer(GameObject parent, SplatterableSprite sprite) {
		super(parent);
		this.transform = parent.getComponent(Transform.class);
		this.sprite = sprite;
	}
	
	@Override
	public boolean update() {
		sprite.update();
		return false;
	}
	
	@Override
	public void render(Graphics2D g) {
		sprite.render(g, transform.x, transform.y, transform.sx, transform.sy);
	}

	public void splatter(Color color, float sx, float sy, float cx, float cy, float vx, float vy) {
		sprite.splatter(color, sx, sy, cx, cy, vx, vy);
	}
	
	public Color getColor() {
		return sprite.getColor();
	}
	
}
