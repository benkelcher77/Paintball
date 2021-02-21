package com.ben.paintball.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Random;

import com.ben.paintball.Game;
import com.ben.paintball.ecs.GameObject;
import com.ben.paintball.ecs.components.BoxBounds;
import com.ben.paintball.ecs.components.ButtonController;
import com.ben.paintball.ecs.components.ButtonRenderer;
import com.ben.paintball.ecs.components.TextFieldController;
import com.ben.paintball.ecs.components.TextFieldRenderer;
import com.ben.paintball.ecs.components.Transform;
import com.ben.paintball.layers.Layer;

public class MultiplayerMenuLayer extends Layer {
	
	private Game game;
	
	private GameObject createServerButton;
	private GameObject joinServerButton;
	private GameObject backButton;
	private GameObject ipTextField;
	private GameObject portTextField;
	private GameObject colorSelector;
	private Font menuFont;

	private boolean team = false; // False for blue/right, True for red/left
	
	public MultiplayerMenuLayer(String name, Game game) {
		super(name);
		
		this.game = game;
	}
	
	@Override
	public void init() {
		menuFont = new Font("Consolas", Font.PLAIN, 14);
		
		ipTextField = new GameObject();
		ipTextField.addComponent(new Transform(ipTextField, 100, 100, 256, 24));
		ipTextField.addComponent(new BoxBounds(ipTextField));
		ipTextField.addComponent(new TextFieldController(ipTextField));
		ipTextField.addComponent(new TextFieldRenderer(ipTextField, "IP Address", Color.BLACK, Color.WHITE, Color.LIGHT_GRAY, menuFont));
		
		portTextField = new GameObject();
		portTextField.addComponent(new Transform(portTextField, 100, 130, 256, 24));
		portTextField.addComponent(new BoxBounds(portTextField));
		portTextField.addComponent(new TextFieldController(portTextField));
		portTextField.addComponent(new TextFieldRenderer(portTextField, "Port", Color.BLACK, Color.WHITE, Color.LIGHT_GRAY, menuFont));
		
		createServerButton = new GameObject();
		createServerButton.addComponent(new Transform(createServerButton, 100, 160, 108, 24));
		createServerButton.addComponent(new BoxBounds(createServerButton));
		createServerButton.addComponent(new ButtonController(createServerButton, (source) -> createServer()));
		createServerButton.addComponent(new ButtonRenderer(createServerButton, "Create Server", Color.BLACK, Color.WHITE, Color.GRAY, menuFont));
		
		joinServerButton = new GameObject();
		joinServerButton.addComponent(new Transform(joinServerButton, 210, 160, 108, 24));
		joinServerButton.addComponent(new BoxBounds(joinServerButton));
		joinServerButton.addComponent(new ButtonController(joinServerButton, (source) -> joinServer()));
		joinServerButton.addComponent(new ButtonRenderer(joinServerButton, "Join Server", Color.BLACK, Color.WHITE, Color.GRAY, menuFont));
		
		backButton = new GameObject();
		backButton.addComponent(new Transform(backButton, 100, 190, 96, 24));
		backButton.addComponent(new BoxBounds(backButton));
		backButton.addComponent(new ButtonController(backButton, (source) -> toMainMenu()));
		backButton.addComponent(new ButtonRenderer(backButton, "Back", Color.BLACK, Color.WHITE, Color.GRAY, menuFont));
	}
	
	@Override
	public void update() {
		createServerButton.update();
		joinServerButton.update();
		backButton.update();
		ipTextField.update();
		portTextField.update();
	}
	
	@Override
	public void render(Graphics2D g) {
		createServerButton.render(g);
		joinServerButton.render(g);
		backButton.render(g);
		ipTextField.render(g);
		portTextField.render(g);
	}
	
	public String getIP() {
		return ipTextField.getComponent(TextFieldController.class).getText();
	}
	
	public int getPort() {
		return Integer.parseInt(portTextField.getComponent(TextFieldController.class).getText());
	}
	
	public boolean getTeam() {
		return team;
	}
	
	public Color getColor() {
		Random random = new Random();
		final float hue = random.nextFloat();
		final float saturation = 0.9f; // 1.0 for brilliant, 0.0 for dull
		final float brightness = 1.0f;  // 1.0 for brighter, 0.0 for black
		return Color.getHSBColor(hue, saturation, brightness);
	}
	
	private void createServer() {
		game.createServer(getPort());
		game.setMultiplayerStartFlag(true);
		team = true;
	}
	
	private void joinServer() {
		game.setMultiplayerStartFlag(true);
		team = false;
	}
	
	private void toMainMenu() {
		game.setToMainMenuFlag(true);
	}

}
