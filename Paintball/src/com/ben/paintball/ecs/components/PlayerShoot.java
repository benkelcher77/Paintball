package com.ben.paintball.ecs.components;

import java.awt.Color;
import java.awt.event.MouseEvent;

import com.ben.paintball.ecs.Component;
import com.ben.paintball.ecs.GameObject;
import com.ben.paintball.game.Handler;
import com.ben.paintball.graphics.SplatterableSprite;
import com.ben.paintball.graphics.Sprite;
import com.ben.paintball.input.Mouse;
import com.ben.paintball.net.packets.Packet03Shoot;
import com.ben.paintball.net.packets.Packet04Grenade;

public class PlayerShoot extends Component {

	private static final float BULLET_SPEED = 20f;
	private static final float GRENADE_SPEED = 5f;
	
	private Handler handler;
	private boolean leftclickFlag = false;
	private boolean rightclickFlag = false;
	
	private int timer;
	private int rifleTimerMax = 6; // 0.1 seconds
	private int shotgunTimerMax = 90; // 1.5 seconds
	private int grenadeTimerMax = 180; // 3 seconds
	
	private boolean autoRifle = false;
	
	private HotbarController hotbar;
	
	public PlayerShoot(GameObject parent, Handler handler) {
		super(parent);
		this.handler = handler;
	}

	@Override
	public boolean update() {
		timer++;
		int timerMax = hotbar.getSelection() == 0 ? rifleTimerMax : (hotbar.getSelection() == 1 ? shotgunTimerMax : grenadeTimerMax);
		if (Mouse.instance.isButtonPressed(MouseEvent.BUTTON1) && ((hotbar.getSelection() == 0 && autoRifle) || !leftclickFlag) && timer >= timerMax) {
			timer = 0;
			if (hotbar.getSelection() == 0) rifle();
			else if (hotbar.getSelection() == 1) shotgun(10, 15f);
			else if (hotbar.getSelection() == 2) grenade();
			leftclickFlag = true;
		} else if (!Mouse.instance.isButtonPressed(MouseEvent.BUTTON1) && leftclickFlag)
			leftclickFlag = false;
		
		if (hotbar.getSelection() == 0 && Mouse.instance.isButtonPressed(MouseEvent.BUTTON3) && !rightclickFlag) {
			rightclickFlag = true;
			autoRifle = !autoRifle;
		} else if (hotbar.getSelection() == 0 && !Mouse.instance.isButtonPressed(MouseEvent.BUTTON3) && rightclickFlag)
			rightclickFlag = false;
		
		return false;
	}

	private void rifle() {
		GameObject camera = handler.getByName("Camera");

		Transform t = parent.getComponent(Transform.class);
		float cx = t.x + t.sx / 2f;
		float cy = t.y + t.sy / 2f;

		float mx = (float) Mouse.instance.getScreenX() + camera.getComponent(CameraFollow.class).getXOffs();
		float my = (float) Mouse.instance.getScreenY() + camera.getComponent(CameraFollow.class).getYOffs();

		float angle = (float) Math.atan2(my - cy, mx - cx);
		if (autoRifle)
			angle += (float)(Math.random() * 2f - 1f) * (float)Math.toRadians(7.5f);
		
		float paintballVx = (float) Math.cos(angle) * BULLET_SPEED;
		float paintballVy = (float) Math.sin(angle) * BULLET_SPEED;

		GameObject paintball = new GameObject();
		paintball.addComponent(new Transform(paintball, cx, cy, 8, 8));
		paintball.addComponent(new BoxBounds(paintball));
		paintball.addComponent(new ResizedBoxBounds(paintball, 128, 128));
		paintball.addComponent(new SpriteRenderer(paintball, new Sprite(parent.getComponent(SplatterableSpriteRenderer.class).getColor().darker())));
		paintball.addComponent(new PaintballController(paintball, parent, handler.getObjects(), paintballVx, paintballVy));
		
		handler.addToQueue(paintball);
		
		if (parent.getComponent(NetClient.class) != null) {
			Packet03Shoot packet = new Packet03Shoot(parent.getComponent(NetClient.class).getID(), cx, cy, paintballVx, paintballVy);
			packet.writeData(parent.getComponent(NetClient.class).getClient());
		}
	}
	
	// Spread in degrees, on either side of angle
	private void shotgun(int numPellets, float spread) {
		GameObject camera = handler.getByName("Camera");

		Transform t = parent.getComponent(Transform.class);
		float cx = t.x + t.sx / 2f;
		float cy = t.y + t.sy / 2f;

		float mx = (float)Mouse.instance.getScreenX() + camera.getComponent(CameraFollow.class).getXOffs();
		float my = (float)Mouse.instance.getScreenY() + camera.getComponent(CameraFollow.class).getYOffs();

		float angle = (float)Math.atan2(my - cy, mx - cx);

		for (int i = 0; i < numPellets; i++) {
			float modifiedAngle = angle + (float)(Math.random() * 2f - 1f) * (float)Math.toRadians(spread);
			//System.out.println(Math.toDegrees(angle) + ":" + Math.toDegrees(modifiedAngle));
			float paintballVx = (float)Math.cos(modifiedAngle) * BULLET_SPEED;
			float paintballVy = (float)Math.sin(modifiedAngle) * BULLET_SPEED;
			GameObject paintball = new GameObject("Paintball " + System.currentTimeMillis());
			paintball.addComponent(new Transform(paintball, cx, cy, 8, 8));
			paintball.addComponent(new BoxBounds(paintball));
			paintball.addComponent(new ResizedBoxBounds(paintball, 128, 128));
			paintball.addComponent(new SpriteRenderer(paintball, new Sprite(parent.getComponent(SplatterableSpriteRenderer.class).getColor().darker())));
			paintball.addComponent(new PaintballController(paintball, parent, handler.getObjects(), paintballVx, paintballVy));
			handler.addToQueue(paintball);
			
			if (parent.getComponent(NetClient.class) != null) {
				Packet03Shoot packet = new Packet03Shoot(parent.getComponent(NetClient.class).getID(), cx, cy, paintballVx, paintballVy);
				packet.writeData(parent.getComponent(NetClient.class).getClient());
			}
		}
	}
	
	private void grenade() {
		GameObject camera = handler.getByName("Camera");

		Transform t = parent.getComponent(Transform.class);
		float cx = t.x + t.sx / 2f;
		float cy = t.y + t.sy / 2f;

		float mx = (float) Mouse.instance.getScreenX() + camera.getComponent(CameraFollow.class).getXOffs();
		float my = (float) Mouse.instance.getScreenY() + camera.getComponent(CameraFollow.class).getYOffs();

		float angle = (float) Math.atan2(my - cy, mx - cx);
		float grenadeVx = (float) Math.cos(angle) * GRENADE_SPEED;
		float grenadeVy = (float) Math.sin(angle) * GRENADE_SPEED;
		
		GameObject grenade = new GameObject("Grenade " + System.currentTimeMillis());
		grenade.addComponent(new Transform(grenade, cx, cy, 12, 12));
		grenade.addComponent(new BoxBounds(grenade));
		grenade.addComponent(new SpriteRenderer(grenade, new Sprite(parent.getComponent(SplatterableSpriteRenderer.class).getColor().darker())));
		grenade.addComponent(new GrenadeController(grenade, parent, handler, parent.getComponent(SplatterableSpriteRenderer.class).getColor().darker(), grenadeVx, grenadeVy, 1.02f, 180));
		handler.addToQueue(grenade);
		
		if (parent.getComponent(NetClient.class) != null) {
			Packet04Grenade packet = new Packet04Grenade(parent.getComponent(NetClient.class).getID(), cx, cy, grenadeVx, grenadeVy);
			packet.writeData(parent.getComponent(NetClient.class).getClient());
		}
	}
	
	public void setHotbar(HotbarController hotbar) {
		this.hotbar = hotbar;
	}

}
