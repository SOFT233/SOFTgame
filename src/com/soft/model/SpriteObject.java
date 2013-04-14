package com.soft.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class SpriteObject {
	
	private Bitmap bitmap;
	private double x;
	private double y;
	private double x_move = 0;
	private double y_move = 0;
	private int state = 1;
	public static final int DEAD = 0;
	public static final int ALIVE = 1;

	public SpriteObject(Bitmap bitmap, double x, double y) {
		super();
		this.bitmap = bitmap;
		this.x = x;
		this.y = y;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	public double getMoveX() {
		return x_move;
	}

	public void setMoveX(double x_move) {
		this.x_move = x_move;
	}

	public double getMoveY() {
		return y_move;
	}

	public void setMoveY(double y_move) {
		this.y_move = y_move;
	}
	
	public Rect getBounds() {
		return new Rect((int)x, (int)y, (int)x+this.getBitmap().getWidth(), (int)y+this.getBitmap().getHeight());
	}
	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	
	public void draw(Canvas canvas) {
		canvas.drawBitmap(bitmap, (float)x, (float)y, null);
		
		//-(bitmap.getHeight()/2)
		//-(bitmap.getWidth()/2)
	}
	
	public void update(int adj_mov) {
		x += (adj_mov * x_move);
		y += (adj_mov * y_move);
	}
	
	public boolean collide(SpriteObject entity) {
		double left, entity_left;
		double right, entity_right;
		double top, entity_top;
		double bottom, entity_bottom;
		
		left = x;
		entity_left = entity.getX();
		
		right = x + bitmap.getWidth();
		entity_right = entity.getX() + entity.getBitmap().getWidth();
		
		top = y;
		entity_top = entity.getY();
		
		bottom = y + bitmap.getHeight();
		entity_bottom = entity.getY() + entity.getBitmap().getHeight();
		
		if(bottom < entity_top) {
			return false;
		}
		if(top > entity_bottom) {
			return false;
		}
		if(right < entity_left) {
			return false;
		}
		if(left > entity_right) {
			return false;
		}
		
		return true;
	}
}
