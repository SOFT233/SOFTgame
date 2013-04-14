package com.soft.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Block extends SpriteObject {
	
	private Bitmap bitmap2;
	private int state = 1;
	public static final int DEAD = 0;
	public static final int ALIVE = 1;

	public Block(Bitmap bitmap, Bitmap bitmap2, double x, double y) {
		super(bitmap, x, y);
		
		this.bitmap2 = bitmap2;
	}
	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public void draw(Canvas canvas) {
		if(state == ALIVE) {
			canvas.drawBitmap(super.getBitmap(), (float)super.getX()-(super.getBitmap().getHeight()/2), (float)super.getY()-(super.getBitmap().getWidth()/2), null);
		}
		else if(state == DEAD) {
			canvas.drawBitmap(bitmap2, (float)super.getX()-(bitmap2.getHeight()/2), (float)super.getY()-(bitmap2.getWidth()/2), null);
		}
	}

	@Override
	public boolean collide(SpriteObject entity) {
		if(state == ALIVE) {
			return super.collide(entity);
		}
		else {
			return false;
		}
	}
}
