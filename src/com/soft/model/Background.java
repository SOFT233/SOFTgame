package com.soft.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Background extends SpriteObject {

	public Background(Bitmap bitmap, int x, int y) {
		super(bitmap, x, y);
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawBitmap(super.getBitmap(), (float)super.getX(), (float)super.getY(), null);
	}
	
	
}
