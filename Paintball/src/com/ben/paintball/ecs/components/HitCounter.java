package com.ben.paintball.ecs.components;

import com.ben.paintball.ecs.Component;
import com.ben.paintball.ecs.GameObject;

public class HitCounter extends Component {

	private int hitCount = 0;
	
	public HitCounter(GameObject parent) {
		super(parent);
	}
	
	public int getHitCount() {
		return hitCount;
	}
	
	public void incrementHitCount() {
		hitCount++;
		System.out.println(parent + ": Hit " + hitCount + " times");
	}

}
