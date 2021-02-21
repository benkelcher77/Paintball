package com.ben.paintball.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import com.ben.paintball.Game;
import com.ben.paintball.ecs.GameObject;
import com.ben.paintball.ecs.components.BoxBounds;
import com.ben.paintball.ecs.components.ButtonController;
import com.ben.paintball.ecs.components.ButtonRenderer;
import com.ben.paintball.ecs.components.Transform;
import com.ben.paintball.layers.Layer;

public class MainMenuLayer extends Layer {

	private Game game;
	
	private GameObject singleplayerButton;
	private GameObject multiplayerButton;
	private GameObject quitButton;
	private Font buttonFont;
	
	public MainMenuLayer(String name, Game game) {
		super(name);
		
		this.game = game;
	}
	
	@Override
	public void init() {
		buttonFont = new Font("Consolas", Font.PLAIN, 14);
		
		singleplayerButton = new GameObject();
		singleplayerButton.addComponent(new Transform(singleplayerButton, 100, 100, 96, 24));
		singleplayerButton.addComponent(new BoxBounds(singleplayerButton));
		singleplayerButton.addComponent(new ButtonController(singleplayerButton, (source) -> { singleplayer(); }));
		singleplayerButton.addComponent(new ButtonRenderer(singleplayerButton, "Singleplayer", Color.BLACK, Color.WHITE, Color.GRAY, buttonFont));
		
		multiplayerButton = new GameObject();
		multiplayerButton.addComponent(new Transform(multiplayerButton, 100, 130, 96, 24));
		multiplayerButton.addComponent(new BoxBounds(multiplayerButton));
		multiplayerButton.addComponent(new ButtonController(multiplayerButton, (source) -> { multiplayer(); }));
		multiplayerButton.addComponent(new ButtonRenderer(multiplayerButton, "Multiplayer", Color.BLACK, Color.WHITE, Color.GRAY, buttonFont));
		
		quitButton = new GameObject();
		quitButton.addComponent(new Transform(quitButton, 100, 160, 96, 24));
		quitButton.addComponent(new BoxBounds(quitButton));
		quitButton.addComponent(new ButtonController(quitButton, (source) -> { System.exit(0); }));
		quitButton.addComponent(new ButtonRenderer(quitButton, "Quit", Color.BLACK, Color.WHITE, Color.GRAY, buttonFont));
	}
	
	@Override
	public void update() {
		singleplayerButton.update();
		multiplayerButton.update();
		quitButton.update();
	}
	
	@Override
	public void render(Graphics2D g) {
		singleplayerButton.render(g);
		multiplayerButton.render(g);
		quitButton.render(g);
	}
	
	private void singleplayer() {
		game.setSingleplayerStartFlag(true);
	}
	
	private void multiplayer() {
		game.setToMultiplayerMenuFlag(true);
	}

}
