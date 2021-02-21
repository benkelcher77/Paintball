package com.ben.paintball.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Sprite {
	
	private BufferedImage img;
	
	public Sprite(Color color) {
		img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		img.setRGB(0, 0, color.getRGB());
	}
	
	public Sprite(int color) {
		this(new Color(color));
	}
	
	public Sprite(float r, float g, float b) {
		this(new Color(r, g, b));
	}
	
	public Sprite(int r, int g, int b) {
		this(new Color(r, g, b));
	}
	
	public Sprite(String filepath) {
		try {
			this.img = ImageIO.read(new File(filepath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void render(Graphics2D g, float x, float y, int w, int h) {
		g.drawImage(img, (int)x, (int)y, w, h, null);
	}
	
	public BufferedImage getImage() {
		return img;
	}

}
