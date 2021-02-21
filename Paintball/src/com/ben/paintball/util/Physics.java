package com.ben.paintball.util;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import com.ben.paintball.ecs.GameObject;
import com.ben.paintball.ecs.components.Transform;

public class Physics {
	
	private Physics() { }
	
	public static boolean isInSight(GameObject source, Point target, List<Rectangle> collidables, float rayStepSize) {
		Transform pt = source.getComponent(Transform.class);
		int tx = (int)Math.floor(pt.x / 32f);
		int ty = (int)Math.floor(pt.y / 32f);
		
		Rectangle targetTile = new Rectangle(target.x * 32, target.y * 32, 32, 32);
		
		float rx = pt.x;
		float ry = pt.y;
		
		float angle = (float)Math.atan2(target.y - ty, target.x - tx);
		float sinAngle = (float)Math.sin(angle);
		float cosAngle = (float)Math.cos(angle);
		
		do {
			rx += rayStepSize * cosAngle;
			ry += rayStepSize * sinAngle;
			
			for (Rectangle collider : collidables)
				if (collider.contains(rx, ry)) // Path is blocked
					return false;
			
		} while (!targetTile.contains(rx, ry));
		
		return true;
	}

}
