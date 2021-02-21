package com.ben.paintball.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import com.ben.paintball.Game;
import com.ben.paintball.ecs.GameObject;
import com.ben.paintball.ecs.components.HitCounter;
import com.ben.paintball.ecs.components.HotbarController;
import com.ben.paintball.ecs.components.HotbarRenderer;
import com.ben.paintball.ecs.components.PlayerShoot;
import com.ben.paintball.ecs.components.Transform;
import com.ben.paintball.layers.Layer;

public class HUDLayer extends Layer {

	private Font hud = new Font("Consolas", Font.PLAIN, 24);
	
	private GameObject player;
	private HitCounter[] enemies;
	
	private GameObject hotbar;
	
	public HUDLayer(String name, GameObject player, GameObject[] enemies) {
		super(name);
		this.player = player;
		
		this.enemies = new HitCounter[enemies.length];
		for (int i = 0; i < enemies.length; i++)
			if (enemies[i] != null)
				this.enemies[i] = enemies[i].getComponent(HitCounter.class);
	}
	
	@Override
	public void init() {
		hotbar = new GameObject("Hotbar");
		hotbar.addComponent(new Transform(hotbar, Game.WIDTH - 96, Game.HEIGHT - 30, 90, 24));
		hotbar.addComponent(new HotbarController(hotbar));
		hotbar.addComponent(new HotbarRenderer(hotbar, "assets/sprites/rifle-24.png", "", "assets/sprites/grenade-24.png"));
		
		player.getComponent(PlayerShoot.class).setHotbar(hotbar.getComponent(HotbarController.class));
	}
	
	@Override
	public void update() {
		hotbar.update();
	}
	
	@Override
	public void render(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.setFont(hud);
		g.drawString("Player Hits: " + player.getComponent(HitCounter.class).getHitCount(), 5, 20);
		for (int i = 0; i < enemies.length; i++)
			if (enemies[i] != null)
				g.drawString("Enemy " + (i + 1) + " Hits: " + enemies[i].getHitCount(), 5, 40 + 20 * i);
		
		
		hotbar.render(g);
	}

}
