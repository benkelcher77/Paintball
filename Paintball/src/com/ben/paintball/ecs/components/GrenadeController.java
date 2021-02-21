package com.ben.paintball.ecs.components;

import java.awt.Color;
import java.util.Random;

import com.ben.paintball.ecs.Component;
import com.ben.paintball.ecs.GameObject;
import com.ben.paintball.game.Handler;
import com.ben.paintball.graphics.Sprite;

public class GrenadeController extends Component {

	private static final float FRAGMENT_SPEED = 20f;
	
	private Transform transform;
	private BoxBounds collider;
	private GameObject source;
	private Handler handler;
	private Color color;
	private float vx, vy;
	private float damping;
	private int delay;
	
	private int timer = 0;
	private Random random;
	
	public GrenadeController(GameObject parent, GameObject source, Handler handler, Color color, float vx, float vy, float damping, int delay) {
		super(parent);
		
		this.transform = parent.getComponent(Transform.class);
		this.collider = parent.getComponent(BoxBounds.class);
		this.source = source;
		this.handler = handler;
		this.color = color;
		this.vx = vx;
		this.vy = vy;
		this.damping = damping;
		this.delay = delay;
		
		this.random = new Random();
	}
	
	@Override
	public boolean update() {
		timer++;
		if (timer >= delay) {
			explode();
			return true;
		}
		
		for (GameObject go : handler.getObjects()) {
			if (go.equals(parent) || go.equals(source) || go.getComponent(PaintballController.class) != null)
				continue;
			
			if (go.getComponent(BoxBounds.class) != null) {
				BoxBounds bb = go.getComponent(BoxBounds.class);
				if (collider.getBoundsTop().intersects(bb.getBounds()) || collider.getBoundsBottom().intersects(bb.getBounds())) {
					vy *= -1;
					break;
				}
				
				if (collider.getBoundsLeft().intersects(bb.getBounds()) || collider.getBoundsRight().intersects(bb.getBounds())) {
					vx *= -1;
					break;
				}
			}
		}
		
		transform.x += vx;
		transform.y += vy;
		
		vx /= damping;
		vy /= damping;
		
		return false;
	}
	
	private void explode() {
		float cx = transform.x + transform.sx / 2f;
		float cy = transform.y + transform.sy / 2f;
		
		for (int i = 0; i < 100; i++) {
			float angle = (float)Math.toRadians(random.nextInt(360));
			float paintballVx = (float) Math.cos(angle) * FRAGMENT_SPEED * 1.2f;
			float paintballVy = (float) Math.sin(angle) * FRAGMENT_SPEED * 1.2f;

			GameObject paintball = new GameObject();
			paintball.addComponent(new Transform(paintball, cx, cy, 8, 8));
			paintball.addComponent(new BoxBounds(paintball));
			paintball.addComponent(new ResizedBoxBounds(paintball, 128, 128));
			paintball.addComponent(new SpriteRenderer(paintball, new Sprite(color)));
			paintball.addComponent(new PaintballController(paintball, parent, handler.getObjects(), paintballVx, paintballVy));

			handler.addToQueue(paintball);
		}
	}

}
