package com.ben.paintball.ecs.components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.ben.paintball.ecs.Component;
import com.ben.paintball.ecs.GameObject;
import com.ben.paintball.game.Handler;
import com.ben.paintball.game.Map;
import com.ben.paintball.graphics.Sprite;
import com.ben.paintball.input.Keyboard;
import com.ben.paintball.util.AStar;
import com.ben.paintball.util.Node;
import com.ben.paintball.util.Physics;

public class EnemyAI extends Component {

	private static final float BULLET_SPEED = 20f;
	private static final float GRENADE_SPEED = 5f;
	
	private enum State {
		Wander,
		Chase,
		Cover,
		CoverFromGrenade
	}
	
	private Transform transform;
	private BoxBounds collider;
	private Handler handler;
	private Map map;
	private int[][] pathfindingMap;
	private List<Point> coverPoints;
	private List<Rectangle> collidables;
	private float vx, vy;
	private float moveSpeed = 3f;
			
	private Random random;

	private State state;
	
	private int wanderTargetTimer = 0;
	private int maxWanderTargetTimer = 600; // 10 seconds
	
	private int chaseTargetTimer = 0;
	private int maxChaseTargetTimer = 300; // 5 seconds
	
	private int shootTimer = 0;
	private int maxShootTimer = 15; // 1/4 seconds
	
	private int checkForGrenadeTimer = 0;
	private int maxCheckForGrenadeTimer = 60; // 1 second
	
	private int coverFromGrenadeTimer = 0;
	private int maxCoverFromGrenadeTimer = 180; // 3 seconds
	
	private List<Node> path;
	
	private GameObject player;
	
	public EnemyAI(GameObject parent, GameObject player, Handler handler, Map map) {
		super(parent);
		
		this.transform = parent.getComponent(Transform.class);
		this.collider = parent.getComponent(BoxBounds.class);
		this.handler = handler;
		this.map = map;
		this.pathfindingMap = map.getPathfindingMap();
		this.coverPoints = map.getCoverPoints();
		this.random = new Random();
		this.path = new ArrayList<Node>();
		this.vx = 0f;
		this.vy = 0f;

		this.collidables = new ArrayList<>();
		for (GameObject go : map.getTiles())
			if (go.getComponent(BoxBounds.class) != null)
				this.collidables.add(go.getComponent(BoxBounds.class).getBounds());
		
		this.player = player;
		this.state = State.Wander;
		
		selectWanderTarget();
		path.remove(0); // For some reason the first node has to be removed otherwise the AIs don't move.
	}

	@Override
	public boolean update() {
		switch (state) {
		case Wander:
			wanderTargetTimer++;
			if (wanderTargetTimer >= maxWanderTargetTimer) {
				wanderTargetTimer = 0;
				selectWanderTarget();
			}
			
			if (!path.isEmpty()) {
				int tx = (int)Math.floor(transform.x / 32f);
				int ty = (int)Math.floor(transform.y / 32f);
				Node next = path.get(0);
				
				float targetX = next.x * 32f;
				float targetY = next.y * 32f;
				
				float diffX = transform.x - targetX;
				float diffY = transform.y - targetY;
				vx = (diffX < -moveSpeed ? moveSpeed : (diffX > moveSpeed ? -moveSpeed : 0f));
				vy = (diffY < -moveSpeed ? moveSpeed : (diffY > moveSpeed ? -moveSpeed : 0f));
				
				if ((tx == next.x && ty == next.y) || next.targetRectangle.intersects(parent.getComponent(BoxBounds.class).getBounds()))
					path.remove(next);
				
				if (path.size() == 0) 
					selectWanderTarget();
			}
			break;
			
		case Chase:
			chaseTargetTimer++;
			shootTimer++;
			if (chaseTargetTimer >= maxChaseTargetTimer) {
				chaseTargetTimer = 0;
				retargetPlayer();
			}
			
			if (shootTimer >= maxShootTimer && Physics.isInSight(parent, new Point((int)Math.floor(player.getComponent(Transform.class).x / 32f), (int)Math.floor(player.getComponent(Transform.class).y / 32f)), collidables, 2.0f)) {
				shootTimer = 0;
				if (random.nextInt(50) == 0)
					grenade(); // TODO: Proper grenade logic
				else
					rifle();
			}
			
			if (!path.isEmpty()) {
				int tx = (int)Math.floor(transform.x / 32f);
				int ty = (int)Math.floor(transform.y / 32f);
				Node next = path.get(0);
				
				float targetX = next.x * 32f;
				float targetY = next.y * 32f;
				
				float diffX = transform.x - targetX;
				float diffY = transform.y - targetY;
				vx = (diffX < -moveSpeed ? moveSpeed : (diffX > moveSpeed ? -moveSpeed : 0f));
				vy = (diffY < -moveSpeed ? moveSpeed : (diffY > moveSpeed ? -moveSpeed : 0f));
				
				if ((tx == next.x && ty == next.y) || next.targetRectangle.intersects(parent.getComponent(BoxBounds.class).getBounds()))
					path.remove(next);
				
				if (path.size() == 0) 
					selectWanderTarget();
			}
			break;
			
		case Cover:
			shootTimer++;
			if (shootTimer >= maxShootTimer && Physics.isInSight(parent, new Point((int)Math.floor(player.getComponent(Transform.class).x / 32f), (int)Math.floor(player.getComponent(Transform.class).y / 32f)), collidables, 2.0f)) {
				shootTimer = 0;
				rifle();
			}
			
			if (!path.isEmpty()) {
				int tx = (int)Math.floor(transform.x / 32f);
				int ty = (int)Math.floor(transform.y / 32f);
				Node next = path.get(0);
				
				float targetX = next.x * 32f;
				float targetY = next.y * 32f;
				
				float diffX = transform.x - targetX;
				float diffY = transform.y - targetY;
				vx = (diffX < -moveSpeed ? moveSpeed : (diffX > moveSpeed ? -moveSpeed : 0f));
				vy = (diffY < -moveSpeed ? moveSpeed : (diffY > moveSpeed ? -moveSpeed : 0f));
				
				if ((tx == next.x && ty == next.y) || next.targetRectangle.intersects(parent.getComponent(BoxBounds.class).getBounds()))
					path.remove(next);
				
				if (path.size() == 0) 
					selectWanderTarget();
			}
			break;
			
		case CoverFromGrenade:
			coverFromGrenadeTimer++;
			shootTimer++;
			if (coverFromGrenadeTimer >= maxCoverFromGrenadeTimer) {
				coverFromGrenadeTimer = 0;
				state = State.Wander;
			}
			
			if (shootTimer >= maxShootTimer && Physics.isInSight(parent, new Point((int)Math.floor(player.getComponent(Transform.class).x / 32f), (int)Math.floor(player.getComponent(Transform.class).y / 32f)), collidables, 2.0f)) {
				shootTimer = 0;
				rifle();
			}
			
			if (!path.isEmpty()) {
				int tx = (int)Math.floor(transform.x / 32f);
				int ty = (int)Math.floor(transform.y / 32f);
				Node next = path.get(0);
				
				float targetX = next.x * 32f;
				float targetY = next.y * 32f;
				
				float diffX = transform.x - targetX;
				float diffY = transform.y - targetY;
				vx = (diffX < -moveSpeed ? moveSpeed : (diffX > moveSpeed ? -moveSpeed : 0f));
				vy = (diffY < -moveSpeed ? moveSpeed : (diffY > moveSpeed ? -moveSpeed : 0f));
				
				if ((tx == next.x && ty == next.y) || next.targetRectangle.intersects(parent.getComponent(BoxBounds.class).getBounds()))
					path.remove(next);
			} else {
				vx = 0f;
				vy = 0f;
			}
			
			break;
		}
		
		for (GameObject go : handler.getObjects()) {
			if (go.equals(parent) || go.getComponent(PaintballController.class) != null || go.getComponent(GrenadeController.class) != null || go.getComponent(EnemyAI.class) != null)
				continue;
			
			if (go.getComponent(BoxBounds.class) != null) {
				BoxBounds bb = go.getComponent(BoxBounds.class);
				Transform other = go.getComponent(Transform.class);
				if (collider.getBoundsLeft().intersects(bb.getBounds())) {
					//System.out.println("Collision On Left");
					vx = 0;
					transform.x = other.x + other.sx;
				} else if (collider.getBoundsRight().intersects(bb.getBounds())) {
					//System.out.println("Collision On Right");
					vx = 0;
					transform.x = other.x - transform.sx;
				}
				
				if (collider.getBoundsTop().intersects(bb.getBounds())) {
					//System.out.println("Collision On Top");
					vy = 0;
					transform.y = other.y + other.sy;
				} else if (collider.getBoundsBottom().intersects(bb.getBounds())) {
					//System.out.println("Collision On Bottom");
					vy = 0;
					transform.y = other.y - transform.sy;
				}
			}
		}
		
		if (state != State.CoverFromGrenade) {
			if (distToPlayer() <= 7 * 32f && state == State.Chase) {
				state = State.Cover;
				chooseCoverPoint(player);
			} else if (distToPlayer() <= 13 * 32f && state == State.Wander || distToPlayer() <= 10 * 32f && distToPlayer() > 7 * 32f && state == State.Cover) {
				state = State.Chase;
			} else if (distToPlayer() > 13 * 32f && state == State.Chase || distToPlayer() > 20 * 32f && state == State.Cover) {
				state = State.Wander;
			}
		}
		
		if (state == State.Wander && vx == 0f && vy == 0f)
			selectWanderTarget();
		
		checkForGrenadeTimer++;
		if (checkForGrenadeTimer >= maxCheckForGrenadeTimer) {
			checkForGrenadeTimer = 0;
			checkForGrenade();
		}
		
		//System.out.println(state + ": " + vx + ", " + vy);
		
		if (parent.getComponent(HitCounter.class) != null) {
			if (parent.getComponent(HitCounter.class).getHitCount() >= 10) {
				float cx = transform.x + transform.sx / 2f;
				float cy = transform.y + transform.sy / 2f;
				
				for (int i = 0; i < 100; i++) {
					float angle = (float)Math.toRadians(random.nextInt(360));
					float paintballVx = (float) Math.cos(angle) * BULLET_SPEED * 1.2f;
					float paintballVy = (float) Math.sin(angle) * BULLET_SPEED * 1.2f;
	
					GameObject paintball = new GameObject();
					paintball.addComponent(new Transform(paintball, cx, cy, 8, 8));
					paintball.addComponent(new BoxBounds(paintball));
					paintball.addComponent(new ResizedBoxBounds(paintball, 128, 128));
					paintball.addComponent(new SpriteRenderer(paintball, new Sprite(Color.BLUE)));
					paintball.addComponent(new PaintballController(paintball, parent, handler.getObjects(), paintballVx, paintballVy));
	
					handler.addToQueue(paintball);
				}
				return true;
			}
		}
		
		transform.x += vx;
		transform.y += vy;
		
		return false;
	}
	
	@Override
	public void render(Graphics2D g) {
		if (Keyboard.instance.isKeyDown(KeyEvent.VK_P)) {
			for (Node n : path) {
				g.setColor(Color.RED);
				g.fill(n.targetRectangle);
			}
		}
	}
	
	private void selectWanderTarget() {
		int candidateX = -1;
		int candidateY = -1;
		
		do {
			int y = random.nextInt(pathfindingMap.length);
			int x = random.nextInt(pathfindingMap[y].length);
			
			if (pathfindingMap[y][x] != -1) {
				candidateX = x;
				candidateY = y;
			}
		} while (candidateX == -1 || candidateY == -1);
				
		
		if (path != null)
			path.clear();
		AStar pathfinder = new AStar(pathfindingMap, (int)Math.floor(transform.x / 32f), (int)Math.floor(transform.y / 32f), false);
		path = pathfinder.findPathTo(candidateX, candidateY);
	}
	
	private void retargetPlayer() {
		path.clear();
		AStar pathfinder = new AStar(pathfindingMap, (int)Math.floor(transform.x / 32f), (int)Math.floor(transform.y / 32f), false);
		path = pathfinder.findPathTo((int)Math.floor(player.getComponent(Transform.class).x / 32f), (int)Math.floor(player.getComponent(Transform.class).y / 32f));
		
		if (path == null) {
			selectWanderTarget();
			state = State.Wander;
		}
	}
	
	private void chooseCoverPoint(GameObject hideFrom) {
		sortCoverPoints();
		Point selection = null;
		for (Point p : coverPoints) {
			if (!Physics.isInSight(hideFrom, p, collidables, 1f)) {
				selection = p;
				break;
			}
		}
		
		if (selection == null) {
			state = State.Chase;
			return;
		}
		
		path.clear();
		AStar pathfinder = new AStar(pathfindingMap, (int)Math.floor(transform.x / 32f), (int)Math.floor(transform.y / 32f), false);
		path = pathfinder.findPathTo(selection.x, selection.y);
	}
	
	private void rifle() {
		float cx = transform.x + transform.sx / 2f;
		float cy = transform.y + transform.sy / 2f;
		
		float tx = player.getComponent(Transform.class).x + player.getComponent(Transform.class).sx / 2f;
		float ty = player.getComponent(Transform.class).y + player.getComponent(Transform.class).sy / 2f;
		
		float angle = (float) Math.atan2(ty - cy, tx - cx);
		float paintballVx = (float) Math.cos(angle) * BULLET_SPEED;
		float paintballVy = (float) Math.sin(angle) * BULLET_SPEED;

		GameObject paintball = new GameObject();
		paintball.addComponent(new Transform(paintball, cx, cy, 8, 8));
		paintball.addComponent(new BoxBounds(paintball));
		paintball.addComponent(new ResizedBoxBounds(paintball, 128, 128));
		paintball.addComponent(new SpriteRenderer(paintball, new Sprite(parent.getComponent(SplatterableSpriteRenderer.class).getColor().darker())));
		paintball.addComponent(new PaintballController(paintball, parent, handler.getObjects(), paintballVx, paintballVy));

		handler.addToQueue(paintball);
	}
	
	private void grenade() {
		float cx = transform.x + transform.sx / 2f;
		float cy = transform.y + transform.sy / 2f;
		
		float tx = player.getComponent(Transform.class).x + player.getComponent(Transform.class).sx / 2f;
		float ty = player.getComponent(Transform.class).y + player.getComponent(Transform.class).sy / 2f;
		
		float angle = (float) Math.atan2(ty - cy, tx - cx);
		float grenadeVx = (float) Math.cos(angle) * GRENADE_SPEED;
		float grenadeVy = (float) Math.sin(angle) * GRENADE_SPEED;

		GameObject grenade = new GameObject("Grenade " + System.currentTimeMillis());
		grenade.addComponent(new Transform(grenade, cx, cy, 12, 12));
		grenade.addComponent(new BoxBounds(grenade));
		grenade.addComponent(new SpriteRenderer(grenade, new Sprite(parent.getComponent(SplatterableSpriteRenderer.class).getColor().darker())));
		grenade.addComponent(new GrenadeController(grenade, parent, handler, parent.getComponent(SplatterableSpriteRenderer.class).getColor().darker(), grenadeVx, grenadeVy, 1.02f, 180));
		handler.addToQueue(grenade);
	}
	
	private float distToPlayer() {
		return (float)Math.hypot(transform.x - player.getComponent(Transform.class).x, transform.y - player.getComponent(Transform.class).y);
	}
	
	private float distToPoint(Point p) {
		int tx = (int)Math.floor(transform.x / 32f);
		int ty = (int)Math.floor(transform.y / 32f);
		return (float)Math.hypot(tx - p.x, ty - p.y);
	}
	
	private void sortCoverPoints() {
		Collections.sort(coverPoints, (Point p1, Point p2) -> {
			float d1 = distToPoint(p1);
			float d2 = distToPoint(p2);
			
			return d1 < d2 ? -1 : (d1 > d2 ? 1 : 0);
		});
	}
	
	private void checkForGrenade() {
		GameObject grenade = null;
		for (GameObject go : handler.getObjects()) {
			if (go.getComponent(GrenadeController.class) != null) {
				grenade = go;
				break;
			}
		}
		
		if (grenade != null) {
			state = State.CoverFromGrenade;
			chooseCoverPoint(grenade);
		}
	}
	
}
