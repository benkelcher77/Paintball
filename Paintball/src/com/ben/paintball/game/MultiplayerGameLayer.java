package com.ben.paintball.game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;

import com.ben.paintball.Game;
import com.ben.paintball.ecs.GameObject;
import com.ben.paintball.ecs.components.BoxBounds;
import com.ben.paintball.ecs.components.CameraFollow;
import com.ben.paintball.ecs.components.HitCounter;
import com.ben.paintball.ecs.components.NetClient;
import com.ben.paintball.ecs.components.PlayerController;
import com.ben.paintball.ecs.components.PlayerShoot;
import com.ben.paintball.ecs.components.SplatterableSpriteRenderer;
import com.ben.paintball.ecs.components.Transform;
import com.ben.paintball.graphics.SplatterableSprite;
import com.ben.paintball.layers.Layer;
import com.ben.paintball.net.Client;
import com.ben.paintball.net.packets.Packet00Login;

public class MultiplayerGameLayer extends Layer {
	
	private Game game;
	private Client client;
	private String ip;
	private int port;
	
	private Handler handler;
	private GameObject camera;
	private Map map;
	
	private GameObject player;
	private long playerID;
	
	private GameObject enemy1;
	private GameObject enemy2;
	private GameObject enemy3;
	
	private boolean playerDead = false;

	private boolean team;
	private Color color;
	
	public MultiplayerGameLayer(String name, Game game, String ip, int port, boolean team, Color color) {
		super(name);
		this.game = game;
		this.port = port;
		this.ip = ip;
		this.team = team;
		this.color = color;
	}
	
	@Override
	public void init() {
		handler = new Handler();
		map = MapLoader.instance.loadMap("assets/maps/testmap.map");
		map.load(handler);
		
		playerID = System.currentTimeMillis();
		
		Point playerStart = (team ? map.pickRSP() : map.pickBSP());
		
		client = new Client(game, ip, playerID, port);
		client.start();
		
		Packet00Login loginPacket = new Packet00Login(playerID, playerStart.x * 32f, playerStart.y * 32f, color.getRed(), color.getGreen(), color.getBlue());
		loginPacket.writeData(client);		

		player = constructPlayer(playerStart.x, playerStart.y, playerID, color);
		
		camera = new GameObject("Camera");
		camera.addComponent(new CameraFollow(camera, player.getComponent(Transform.class)));
		
		handler.addObject(player, camera);
		
	}
	
	@Override
	public void update() {
		handler.update();
		
		if (handler.getByName("Player") == null && !playerDead) {
			game.setToMultiplayerDeathScreenFlag(true);
			playerDead = true;
		}
	}
	
	@Override
	public void render(Graphics2D g) {
		AffineTransform at = g.getTransform();
		
		g.scale(Game.ZOOM, Game.ZOOM);
		g.translate(-camera.getComponent(CameraFollow.class).getXOffs(), -camera.getComponent(CameraFollow.class).getYOffs());
		
		handler.render(g, camera);
		
		g.setTransform(at);
	}
	
	private GameObject constructPlayer(int tx, int ty, long id, Color color) {
		GameObject player = new GameObject("Player");
		player.addComponent(new Transform(player, tx * 32f, ty * 32f, 32, 32));
		player.addComponent(new SplatterableSpriteRenderer(player, new SplatterableSprite(color, 32, 32, true)));
		player.addComponent(new BoxBounds(player));
		player.addComponent(new PlayerController(player, 5f, handler.getObjects()));
		player.addComponent(new PlayerShoot(player, handler));
		player.addComponent(new NetClient(player, id, client));
		player.addComponent(new HitCounter(player));
		return player;
	}
	
	public void setPlayer(GameObject player) {
		this.player = player;
	}
	
	public GameObject getPlayer() {
		return player;
	}
	
	public long getLocalPlayerID() {
		return playerID;
	}
	
	public GameObject[] getEnemies() {
		return new GameObject[] { enemy1, enemy2, enemy3 };
	}
	
	public Handler getHandler() {
		return handler;
	}

	public int getPort() {
		return port;
	}
	
	public String getIP() {
		return ip;
	}
}
