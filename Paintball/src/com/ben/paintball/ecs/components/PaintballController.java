package com.ben.paintball.ecs.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import com.ben.paintball.ecs.Component;
import com.ben.paintball.ecs.GameObject;

public class PaintballController extends Component {

	private Transform transform;
	private BoxBounds collider;
	
	private GameObject shooter;
	private List<GameObject> objects;
	
	private float vx, vy;
	
	public PaintballController(GameObject parent, GameObject shooter, List<GameObject> objects, float vx, float vy) {
		super(parent);
		this.transform = parent.getComponent(Transform.class);
		this.collider = parent.getComponent(BoxBounds.class);
		
		this.shooter = shooter;
		this.objects = objects;
		this.vx = vx;
		this.vy = vy;
	}
	
	@Override
	public boolean update() {
		for (GameObject go : objects) {
			if (go.equals(parent) || go.equals(shooter) || go.getComponent(PaintballController.class) != null)
				continue;
			
			if (go.getComponent(BoxBounds.class) != null) {
				if (collider.getBounds().intersects(go.getComponent(BoxBounds.class).getBounds())) {
					
					if (go.getComponent(HitCounter.class) != null)
						go.getComponent(HitCounter.class).incrementHitCount();
					
					// Need to splatter all tiles within range TODO: fix range
					Rectangle rbb = parent.getComponent(ResizedBoxBounds.class).getBounds();
					Rectangle splatterRegion = rbb;
						
					List<GameObject> tilesToSplatter = new ArrayList<>();
					for (GameObject go2 : objects) {
						if (go2.equals(parent) || go2.equals(shooter))
							continue;
						
						if (go2.getComponent(SplatterableSpriteRenderer.class) != null) {
							if (go2.getComponent(BoxBounds.class) != null) {
								if (go2.getComponent(BoxBounds.class).getBounds().intersects(splatterRegion)) {
									tilesToSplatter.add(go2);
								}
							}
						}
					}
					
					float velocity = (float)Math.sqrt(vx * vx + vy * vy);
					float xint = parent.getComponent(Transform.class).x + parent.getComponent(Transform.class).sx / 2f;
					float yint = parent.getComponent(Transform.class).y + parent.getComponent(Transform.class).sy / 2f;
					Rectangle bounds = go.getComponent(BoxBounds.class).getBounds();
					while (bounds.contains(xint, yint)) {
						xint -= vx / velocity;
						yint -= vy / velocity;
					}
					
					for (GameObject splatterable : tilesToSplatter)
						splatterable.getComponent(SplatterableSpriteRenderer.class).splatter(new Color(parent.getComponent(SpriteRenderer.class).getSprite().getImage().getRGB(0, 0)), 
												  splatterable.getComponent(Transform.class).x, 
												  splatterable.getComponent(Transform.class).y, 
												  xint, 
												  yint, 
												  vx, vy);
					
					return true;
				}
			}
		}
		
		transform.x += vx;
		transform.y += vy;
		return false;
	}
	
	public GameObject getShooter() {
		return shooter;
	}

}
