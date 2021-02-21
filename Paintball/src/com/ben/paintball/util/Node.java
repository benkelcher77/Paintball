package com.ben.paintball.util;

import java.awt.Rectangle;

public class Node implements Comparable<Object> {

	public Node parent;
	public int x, y;
	public double g;
	public double h;

	public Rectangle targetRectangle;
	
	public Node(Node parent, int xpos, int ypos, double g, double h) {
		this.parent = parent;
		this.x = xpos;
		this.y = ypos;
		this.g = g;
		this.h = h;
		targetRectangle = new Rectangle(0, 0, 0, 0);
	}

	// Compare by f value (g + h)
	@Override
	public int compareTo(Object o) {
		Node that = (Node) o;
		
		if (this.g == that.g && this.h == that.h) return 0;
		return (int) Math.signum(((this.g + this.h) - (that.g + that.h)));
	}
	
}
