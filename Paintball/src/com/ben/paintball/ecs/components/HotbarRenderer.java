package com.ben.paintball.ecs.components;

import java.awt.Color;
import java.awt.Graphics2D;

import com.ben.paintball.ecs.Component;
import com.ben.paintball.ecs.GameObject;
import com.ben.paintball.graphics.Sprite;

public class HotbarRenderer extends Component {

	private Transform transform;
	private HotbarController hotbarController;
	
	private Sprite rifle;
	private Sprite shotgun;
	private Sprite grenade;
	
	public HotbarRenderer(GameObject parent, String riflePath, String shotgunPath, String grenadePath) {
		super(parent);
		
		this.transform = parent.getComponent(Transform.class);
		this.hotbarController = parent.getComponent(HotbarController.class);
		
		this.rifle = new Sprite(riflePath);
		this.grenade = new Sprite(grenadePath);
	}
	
	@Override
	public void render(Graphics2D g) {
		for (int i = 0; i < 3; i++) {
			g.setColor(new Color(0.8f, 0.2f, 0.3f, 1.0f));
			g.fillRect((int)(transform.x + i * 30), (int)transform.y, 24, 24);
			
			if (i == 0)
				rifle.render(g, transform.x + i * 30, transform.y, 24, 24);
			if (i == 2)
				grenade.render(g, transform.x + i * 30, transform.y, 24, 24);
			
			if (i == hotbarController.getSelection()) {
				g.setColor(Color.WHITE);
				g.drawRect((int)(transform.x + i * 30), (int)transform.y, 24, 24);
			}
				
		}
	}

}
