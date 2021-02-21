package com.ben.paintball;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.util.Random;

import javax.swing.JFrame;

import com.ben.paintball.game.SingleplayerDeathLayer;
import com.ben.paintball.game.HUDLayer;
import com.ben.paintball.game.MainMenuLayer;
import com.ben.paintball.game.MultiplayerDeathLayer;
import com.ben.paintball.game.MultiplayerGameLayer;
import com.ben.paintball.game.MultiplayerMenuLayer;
import com.ben.paintball.game.SingleplayerGameLayer;
import com.ben.paintball.input.Keyboard;
import com.ben.paintball.input.Mouse;
import com.ben.paintball.layers.LayerStack;
import com.ben.paintball.net.Server;

public class Game extends Canvas implements Runnable {

	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	public static final float ZOOM = 1.0f;
	
	private static final double UPS = 60.0;
	
	private JFrame frame;
	private String title = "Paintball";
	
	private Thread thread;
	private boolean running = false;
	
	private LayerStack stack;
	
	private boolean startMultiplayerFlag = false;
	private boolean startSingleplayerFlag = false;
	private boolean toMainMenuFlag = false;
	private boolean toSingleplayerDeathScreenFlag = false;
	private boolean toMultiplayerDeathScreenFlag = false;
	private boolean toMultiplayerMenuFlag = false;
	
	private Server server;
	
	public Game() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setMinimumSize(new Dimension(WIDTH, HEIGHT));
		setMaximumSize(new Dimension(WIDTH, HEIGHT));
		
		addKeyListener(Keyboard.instance);
		addMouseListener(Mouse.instance);
		addMouseMotionListener(Mouse.instance);
		addMouseWheelListener(Mouse.instance);
		
		stack = new LayerStack();
		stack.push(new MainMenuLayer("Main Menu", this));
	}
	
	public synchronized void start() {
		if (running)
			return;
		
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public synchronized void stop() {
		if (!running)
			return;
		
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		long now;
		long lastTime = System.nanoTime();
		final double NS = 1000000000.0 / UPS;
		double delta = 0.0;

		long timer = System.currentTimeMillis();
		
		int frames = 0;
		int updates = 0;
		
		requestFocus();
		while (running) {
			now = System.nanoTime();
			delta += (now - lastTime) / NS;
			lastTime = now;
			
			while (delta >= 1.0) {
				update();
				delta--;
				updates++;
			}
			
			render();
			frames++;
			
			if (System.currentTimeMillis() - timer >= 1000) {
				frame.setTitle(title + " | " + frames + "fps, " + updates + "ups");
				timer += 1000;
				frames = 0;
				updates = 0;
			}
		}
		
		stop();
	}
	
	private void update() {
		stack.update();
		Mouse.instance.update();
		
		if (startSingleplayerFlag) {
			stack.pop();
			
			SingleplayerGameLayer sgl = new SingleplayerGameLayer("Game", this);
			stack.push(sgl);
			
			stack.push(new HUDLayer("HUD", sgl.getPlayer(), sgl.getEnemies()));
			
			startSingleplayerFlag = false;
		}
		
		if (startMultiplayerFlag) {
			if (stack.peek() instanceof MultiplayerMenuLayer) {
				MultiplayerMenuLayer mml = (MultiplayerMenuLayer)stack.pop();
				
				MultiplayerGameLayer mgl = new MultiplayerGameLayer("Game", this, mml.getIP(), mml.getPort(), mml.getTeam(), mml.getColor());
				stack.push(mgl);
				stack.push(new HUDLayer("HUD", mgl.getPlayer(), mgl.getEnemies()));
			} else if (stack.peek() instanceof MultiplayerDeathLayer) {
				stack.pop(); // MultiplayerDeathLayer
				MultiplayerGameLayer mgl = (MultiplayerGameLayer)stack.pop(); // MutliplayerGameLayer
				MultiplayerGameLayer newMGL = new MultiplayerGameLayer("Game", this, mgl.getIP(), mgl.getPort(), true, randomColor());
				stack.push(newMGL);
				stack.push(new HUDLayer("HUD", newMGL.getPlayer(), newMGL.getEnemies()));
			}
			
			startMultiplayerFlag = false;
		}
		
		if (toMainMenuFlag) {
			stack.clear();
			stack.push(new MainMenuLayer("Main Menu", this));
			
			toMainMenuFlag = false;
		}
		
		if (toSingleplayerDeathScreenFlag) {
			stack.pop(); // HUDLayer
			stack.push(new SingleplayerDeathLayer("Death", this));
			
			toSingleplayerDeathScreenFlag = false;
		}
		
		if (toMultiplayerDeathScreenFlag) {
			stack.pop(); // HUDLayer
			stack.push(new MultiplayerDeathLayer("Death", this));
			
			toMultiplayerDeathScreenFlag = false;
		}
		
		if (toMultiplayerMenuFlag) {
			stack.pop(); // MainMenuLayer
			stack.push(new MultiplayerMenuLayer("Multiplayer Menu", this));
			
			toMultiplayerMenuFlag = false;
		}
	}
	
	private void render() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}
		
		Graphics2D g = (Graphics2D)bs.getDrawGraphics();
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		stack.render(g);
		
		g.dispose();
		bs.show();
	}
	
	public void setMultiplayerStartFlag(boolean flag) {
		this.startMultiplayerFlag = flag;
	}
	
	public void setSingleplayerStartFlag(boolean flag) {
		this.startSingleplayerFlag = flag;
	}
	
	public void setToMainMenuFlag(boolean flag) {
		this.toMainMenuFlag = flag;
	}
	
	public void setToSingleplayerDeathScreenFlag(boolean flag) {
		this.toSingleplayerDeathScreenFlag = flag;
	}
	
	public void setToMultiplayerMenuFlag(boolean flag) {
		this.toMultiplayerMenuFlag = flag;
	}
	
	public void setToMultiplayerDeathScreenFlag(boolean flag) {
		this.toMultiplayerDeathScreenFlag = flag;
	}
	
	public void createServer(int port) {
		server = new Server(this, port);
		server.start();
	}
	
	public LayerStack getLayerStack() {
		return stack;
	}
	
	private Color randomColor() {
		Random random = new Random();
		final float hue = random.nextFloat();
		final float saturation = 0.9f; // 1.0 for brilliant, 0.0 for dull
		final float brightness = 1.0f;  // 1.0 for brighter, 0.0 for black
		return Color.getHSBColor(hue, saturation, brightness);
	}
	
	public static void main(String[] args) {
		Game game = new Game();
		game.frame = new JFrame("Paintball");
		
		game.frame.add(game);
		game.frame.pack();
		game.frame.setLocationRelativeTo(null);
		game.frame.setResizable(false);
		game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.frame.setVisible(true);
		
		game.start();
	}

}
