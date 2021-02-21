package com.ben.paintball.util;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AStar {

	private final List<Node> open;
	private final List<Node> closed;
	private final List<Node> path;
	private final int[][] maze;
	private Node now;
	private final int xstart;
	private final int ystart;
	private int xend, yend;
	private final boolean diag;

	public AStar(int[][] maze, int xstart, int ystart, boolean diag) {
		this.open = new ArrayList<>();
		this.closed = new ArrayList<>();
		this.path = new ArrayList<>();
		this.maze = maze;
		this.now = new Node(null, xstart, ystart, 0, 0);
		this.xstart = xstart;
		this.ystart = ystart;
		this.diag = diag;
	}

	/**
	 ** Finds path to xend/yend or returns null
	 **
	 ** @param (int) xend coordinates of the target position
	 ** 
	 * @param (int) yend
	 ** 
	 * @return (List<Node> | null) the path
	 */
	public List<Node> findPathTo(int xend, int yend) {
		this.xend = xend;
		this.yend = yend;
		this.closed.add(this.now);
		addNeigborsToOpenList();
		while (this.now.x != this.xend || this.now.y != this.yend) {
			if (this.open.isEmpty()) { // Nothing to examine
				return null;
			}
			this.now = this.open.get(0); // get first node (lowest f score)
			this.open.remove(0); // remove it
			this.closed.add(this.now); // and add to the closed
			addNeigborsToOpenList();
		}
		this.path.add(0, this.now);
		while (this.now.x != this.xstart || this.now.y != this.ystart) {
			this.now = this.now.parent;
			this.path.add(0, this.now);
		}
		
		for (Node n : path) {
			n.targetRectangle = new Rectangle(n.x * 32 + 12, n.y * 32 + 12, 8, 8);
		}
		return this.path;
	}

	/**
	 ** Looks in a given List<> for a node
	 **
	 ** @return (bool) NeightborInListFound
	 */
	private static boolean findNeighborInList(List<Node> array, Node node) {
		return array.stream().anyMatch((n) -> (n.x == node.x && n.y == node.y));
	}

	/**
	 ** Calulate distance between this.now and xend/yend
	 **
	 ** @return (int) distance
	 */
	private double distance(int dx, int dy) {
		if (this.diag) { // if diagonal movement is alloweed
			return Math.hypot(this.now.x + dx - this.xend, this.now.y + dy - this.yend); // return hypothenuse
		} else {
			return Math.abs(this.now.x + dx - this.xend) + Math.abs(this.now.y + dy - this.yend); // else return
																									// "Manhattan
																									// distance"
		}
	}

	private void addNeigborsToOpenList() {
		Node node;
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				if (!this.diag && x != 0 && y != 0) {
					continue; // skip if diagonal movement is not allowed
				}
				node = new Node(this.now, this.now.x + x, this.now.y + y, this.now.g, this.distance(x, y));
				if ((x != 0 || y != 0) // not this.now
						&& this.now.x + x >= 0 && this.now.x + x < this.maze[0].length // check maze boundaries
						&& this.now.y + y >= 0 && this.now.y + y < this.maze.length
						&& this.maze[this.now.y + y][this.now.x + x] != -1 // check if square is walkable
						&& !findNeighborInList(this.open, node) && !findNeighborInList(this.closed, node)) { // if not
																												// already
																												// done
					node.g = node.parent.g + 1.; // Horizontal/vertical cost = 1.0
					node.g += maze[this.now.y + y][this.now.x + x]; // add movement cost for this square

					// diagonal cost = sqrt(hor_cost + vert_cost
					// in this example the cost would be 12.2 instead of 11
					/*
					 * if (diag && x != 0 && y != 0) { node.g += .4; // Diagonal movement cost = 1.4
					 * }
					 */
					this.open.add(node);
				}
			}
		}
		Collections.sort(this.open);
	}

}
