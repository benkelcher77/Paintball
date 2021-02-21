package com.ben.paintball.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

public class SplatterableSprite {
	
	private BufferedImage img;
	private Color original;
	private int w, h;
	
	private Random random;

	private boolean fade;
	private int speed = 1;
	private int fadeTimer = 0;
	private int fadeTimerStartToFadeTrigger = 600; // 10 seconds
	
	public SplatterableSprite(Color color, int w, int h, boolean fade) {
		this.img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		this.w = w;
		this.h = h;
		this.original = color;
		this.fade = fade;
		this.random = new Random();
		
		int[] rgb = new int[w * h];
		for (int i = 0; i < w * h; i++)
			rgb[i] = color.getRGB();
		
		img.setRGB(0, 0, w, h, rgb, 0, 0);
	}
	
	public SplatterableSprite(int color, int w, int h, boolean fade) {
		this(new Color(color), w, h, fade);
	}
	
	public SplatterableSprite(float r, float g, float b, int w, int h, boolean fade) {
		this(new Color(r, g, b), w, h, fade);
	}
	
	public SplatterableSprite(int r, int g, int b, int w, int h, boolean fade) {
		this(new Color(r, g, b), w, h, fade);
	}
	
	public void update() {
		if (fade) {
			fadeTimer++;
			if (fadeTimer > fadeTimerStartToFadeTrigger) {
				
				for (int y = 0; y < h; y++) {
					for (int x = 0; x < w; x++) {
						int pix = img.getRGB(x, y);
						int a = 0xFF & (pix >> 24);
						int r = 0xFF & (pix >> 16);
						int g = 0xFF & (pix >>  8);
						int b = 0xFF & (pix >>  0);
						
						int dr = original.getRed() - r; // negative if r needs to decrease
						int dg = original.getGreen() - g;
						int db = original.getBlue() - b;
						int da = original.getAlpha() - a;
						
						if (dr <= -speed)
							r -= speed;
						else if (dr >= speed)
							r += speed;
						else if (dr < 0 && dr > -speed)
							r = original.getRed();
						else if (dr > 0 && dr < speed)
							r = original.getRed();
						
						if (dg <= -speed)
							g -= speed;
						else if (dg >= speed)
							g += speed;
						else if (dg < 0 && dg > -speed)
							g = original.getGreen();
						else if (dg > 0 && dg < speed)
							g = original.getGreen();
						
						if (db <= -speed)
							b -= speed;
						else if (db >= speed)
							b += speed;
						else if (db < 0 && db > -speed)
							b = original.getBlue();
						else if (db > 0 && db < speed)
							b = original.getBlue();
						
						if (da <= -speed)
							a -= speed;
						else if (da >= speed)
							a += speed;
						else if (da < 0 && da > -speed)
							a = original.getAlpha();
						else if (da > 0 && da < speed)
							a = original.getAlpha();
						
						img.setRGB(x, y, (a << 24) | (r << 16 ) | (g << 8) | b);
					}
				}
			}
		}
	}
	
	public void splatter(Color color, float sx, float sy, float xint, float yint, float vx, float vy) {
		fadeTimer = 0;
		float theta = (float)Math.atan2(vy, vx); // Trajectory of ball
		float angleOfSpread = 180f;
		float theta0 = theta - (float)Math.toRadians(angleOfSpread / 2f);
		float theta1 = theta + (float)Math.toRadians(angleOfSpread / 2f);
		
		float velocity = (float)Math.sqrt(vx * vx + vy * vy);
		
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				
				float dist = (float)Point.distance(x + sx, y + sy, xint, yint);
				float angle = (float)Math.atan2(y + sy - yint, x + sx - xint);
				boolean inCone = (theta0 < theta1 ? angle > theta0 && angle < theta1 : angle > theta1 && angle < theta0);
				float chance = (float)Math.random() / (dist / 40f);
				
				if (dist < velocity && inCone && chance > 1) // TODO: blend with other splatters? das kinda hard tho
					img.setRGB(x, y, color.getRGB());
			}
		}
	}
	
	public void render(Graphics2D g, float x, float y, int w, int h) {
		g.drawImage(img, (int)x, (int)y, w, h, null);
	}
	
	public Color getColor() {
		return original;
	}

}
