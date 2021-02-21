package com.ben.paintball.game;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.ben.paintball.ecs.GameObject;
import com.ben.paintball.ecs.components.BoxBounds;
import com.ben.paintball.ecs.components.SplatterableSpriteRenderer;
import com.ben.paintball.ecs.components.Transform;
import com.ben.paintball.graphics.SplatterableSprite;
import com.ben.paintball.util.Pair;

public class MapLoader {

	public static MapLoader instance = new MapLoader();

	private MapLoader() {
	}

	public Map loadMap(String filepath) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(filepath));
			String line;

			GameObject[] tiles = null;
			int w = 0;
			int h = 0;
			List<Point> rsp = new ArrayList<>();
			List<Point> bsp = new ArrayList<>();
			List<Point> cps = new ArrayList<>();

			while ((line = br.readLine()) != null) {
				String[] toks = line.split(" ");
				if (line.startsWith("TILES")) { // Tile Map
					Pair<GameObject[], Integer[]> tilemap = loadTiles(toks[1]);
					tiles = tilemap.first;
					w = tilemap.second[0];
					h = tilemap.second[1];
				} else if (line.startsWith("RSP")) { // Starting position for Red team, in tiles
					rsp.add(new Point(Integer.parseInt(toks[1]), Integer.parseInt(toks[2])));
				} else if (line.startsWith("BSP")) { // Starting position for Blue team, in tiles
					bsp.add(new Point(Integer.parseInt(toks[1]), Integer.parseInt(toks[2])));
				} else if (line.startsWith("CP")) { // Cover point, in tiles
					cps.add(new Point(Integer.parseInt(toks[1]), Integer.parseInt(toks[2])));
				}
			}

			br.close();

			int[][] pathfindingMap = new int[h][w];
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					pathfindingMap[y][x] = (tiles[x + y * w].getComponent(BoxBounds.class) != null) ? -1 : 0;
				}
			}

//			for (int[] row : pathfindingMap) {
//				for (int cell : row) {
//					switch (cell) {
//					case 0:
//						System.out.print("_");
//						break;
//					case -1:
//						System.out.print("*");
//						break;
//					default:
//						System.out.print("#");
//					}
//				}
//				System.out.println();
//			}

			return new Map(tiles, w, h, rsp, bsp, cps, pathfindingMap);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private Pair<GameObject[], Integer[]> loadTiles(String filepath) {
		try {
			BufferedImage img = ImageIO.read(new File(filepath));
			int w = img.getWidth();
			int h = img.getHeight();
			GameObject[] tilemap = new GameObject[w * h];
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					tilemap[x + y * w] = pixelToTile(x, y, img.getRGB(x, y));
				}
			}

			return new Pair<GameObject[], Integer[]>(tilemap, new Integer[] { w, h });
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private GameObject pixelToTile(int tx, int ty, int pixel) {
		switch (pixel) {
		case 0xFFAAAAAA: // Floor
			return constructTile(tx * 32f, ty * 32f, new SplatterableSprite(Color.LIGHT_GRAY, 32, 32, false), false);
		case 0xFF777777: // Wall
			return constructTile(tx * 32f, ty * 32f, new SplatterableSprite(Color.GRAY, 32, 32, false), true);
		case 0xFF000000: // Empty, Black, Solid
		default:
			return constructTile(tx * 32f, ty * 32f, new SplatterableSprite(Color.BLACK, 32, 32, false), true);
		}
	}

	private GameObject constructTile(float x, float y, SplatterableSprite ss, boolean solid) {
		GameObject tile = new GameObject();
		tile.addComponent(new Transform(tile, x, y, 32, 32));

		if (solid)
			tile.addComponent(new BoxBounds(tile));

		tile.addComponent(new SplatterableSpriteRenderer(tile, ss));

		return tile;
	}

}
