package com.ben.paintball.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import com.ben.paintball.Game;
import com.ben.paintball.input.Keyboard;
import com.ben.paintball.layers.Layer;

public class SingleplayerDeathLayer extends Layer {

	private Game game;
	
	private Font main;
	private Font secondary;
	
	public SingleplayerDeathLayer(String name, Game game) {
		super(name);
		this.game = game;
	}
	
	@Override
	public void init() {
		main = new Font("Consolas", Font.BOLD, 48);
		secondary = new Font("Consolas", Font.PLAIN, 28);
	}
	
	@Override
	public void update() {
		if (Keyboard.instance.isKeyDown(KeyEvent.VK_SPACE))
			game.setToMainMenuFlag(true);
	}
	
	@Override
	public void render(Graphics2D g) {
		g.setColor(new Color(128, 128, 128, 128));
		g.fillRect(0, 0, game.getWidth(), game.getHeight());
		
		g.setColor(Color.WHITE);
		g.setFont(main);
		g.drawString("YOU DIED", 50, 75);
		g.setFont(secondary);
		g.drawString("Press space to return to the main menu", 50, 115);
	}
}
