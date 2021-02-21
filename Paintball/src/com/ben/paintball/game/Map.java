package com.ben.paintball.game;

import java.awt.Point;
import java.util.List;
import java.util.Random;

import com.ben.paintball.ecs.GameObject;

public class Map {
	
	private GameObject[] tiles;
	private int w, h;
	private List<Point> redSPs;
	private List<Point> blueSPs;
	private List<Point> coverPoints;
	private int[][] pathfindingMap;
	
	private Random random;
	
	private int bspIndex = 0;
	private int rspIndex = 0;
	
	public Map(GameObject[] tiles, int w, int h, List<Point> redSPs, List<Point> blueSPs, List<Point> coverPoints, int[][] pathfindingMap) {
		this.tiles = tiles;
		this.w = w;
		this.h = h;
		this.redSPs = redSPs;
		this.blueSPs = blueSPs;
		this.coverPoints = coverPoints;
		this.pathfindingMap = pathfindingMap;
		
		this.random = new Random();
	}
	
	public Point pickRSP() {
		return redSPs.get(rspIndex++ % redSPs.size());
	}
	
	public Point pickBSP() {
		return blueSPs.get(bspIndex++ % blueSPs.size());
	}
	
	public void load(Handler handler) {
		handler.addObject(tiles);
	}
	
	public void delete(Handler handler) {
		handler.removeObject(tiles);
	}
	
	public GameObject[] getTiles() {
		return tiles;
	}
	
	public GameObject getTile(int tx, int ty) {
		return tiles[tx + ty * w];
	}
	
	public int[][] getPathfindingMap() {
		return pathfindingMap;
	}
	
	public List<Point> getCoverPoints() {
		return coverPoints;
	}

}
