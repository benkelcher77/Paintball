package com.ben.paintball.game;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;

import com.ben.paintball.Game;
import com.ben.paintball.ecs.GameObject;
import com.ben.paintball.ecs.components.BoxBounds;
import com.ben.paintball.ecs.components.CameraFollow;
import com.ben.paintball.ecs.components.EnemyAI;
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

public class SingleplayerGameLayer extends Layer {

	private Game game;
	
	private Handler handler;
	private GameObject camera;
	private Map map;
	
	private GameObject player;
	
	private GameObject enemy1;
	private GameObject enemy2;
	private GameObject enemy3;
	
	private boolean playerDead = false;
	
	public SingleplayerGameLayer(String name, Game game) {
		super(name);
		this.game = game;
	}
	
	@Override
	public void init() {
		handler = new Handler();
		map = MapLoader.instance.loadMap("assets/maps/testmap.map");
		map.load(handler);
		
		Point playerStart = map.pickRSP();
		player = constructPlayer(playerStart.x, playerStart.y);
		
		Point enemyStart = map.pickBSP();
		enemy1 = constructEnemy(player, enemyStart.x, enemyStart.y);
		enemyStart = map.pickBSP();
		enemy2 = constructEnemy(player, enemyStart.x, enemyStart.y);
		enemyStart = map.pickBSP();
		enemy3 = constructEnemy(player, enemyStart.x, enemyStart.y);
		
		camera = new GameObject("Camera");
		camera.addComponent(new CameraFollow(camera, player.getComponent(Transform.class)));
		
		handler.addObject(player, enemy1, enemy2, enemy3, camera);
		
	}
	
	@Override
	public void update() {
		handler.update();
		
		if (handler.getByName("Player") == null && !playerDead) {
			game.setToSingleplayerDeathScreenFlag(true);
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
	
	private GameObject constructPlayer(int tx, int ty) {
		GameObject player = new GameObject("Player");
		player.addComponent(new Transform(player, tx * 32f, ty * 32f, 32, 32));
		player.addComponent(new SplatterableSpriteRenderer(player, new SplatterableSprite(1.0f, 0.2f, 0.3f, 32, 32, true)));
		player.addComponent(new BoxBounds(player));
		player.addComponent(new PlayerController(player, 5f, handler.getObjects()));
		player.addComponent(new PlayerShoot(player, handler));
		player.addComponent(new HitCounter(player));
		return player;
	}
	
	private GameObject constructEnemy(GameObject player, int tx, int ty) {
		GameObject enemy = new GameObject("Enemy " + System.currentTimeMillis());
		enemy.addComponent(new Transform(enemy, tx * 32f, ty * 32f, 32, 32));
		enemy.addComponent(new SplatterableSpriteRenderer(enemy, new SplatterableSprite(0.2f, 0.3f, 0.8f, 32, 32, true)));
		enemy.addComponent(new BoxBounds(enemy));
		enemy.addComponent(new EnemyAI(enemy, player, handler, map));
		enemy.addComponent(new HitCounter(enemy));
		return enemy;
	}
	
	public void setPlayer(GameObject player) {
		this.player = player;
	}
	
	public GameObject getPlayer() {
		return player;
	}
	
	public GameObject[] getEnemies() {
		return new GameObject[] { enemy1, enemy2, enemy3 };
	}
	
	public Handler getHandler() {
		return handler;
	}

}
