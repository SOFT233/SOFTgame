package com.soft.softgame;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.soft.model.SpriteObject;
import com.soft.util.GameView;
import com.soft.util.InputObject;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.view.SurfaceHolder;

public class DodgeView extends GameView {
	
	private SpriteObject brt;
	private SpriteObject back;
	private SpriteObject loose;
	private ArrayList<SpriteObject> balls;
	private int id_ball_noise;
	private int id_loose_noise;
	private boolean gameEnd = false;
	private MediaPlayer mp;
	private int score = 0;
	private ArrayList<Point> ballPos;
	private double ballVelocityPos = 0.5;
	private double ballVelocityNeg = -0.5;
	private int ballPosIndex = 0;
	
	public DodgeView(Context context) {
		super(context);
		
		id_ball_noise = this.getSoundPool().load(context, R.raw.ball, 1);
		id_loose_noise = this.getSoundPool().load(context, R.raw.loose, 1);
		
		balls = new ArrayList<SpriteObject>();
		
		ballPos = new ArrayList<Point>();
		
		brt = new SpriteObject(BitmapFactory.decodeResource(getResources(), R.drawable.brt), 1000, 300);
		back = new SpriteObject(BitmapFactory.decodeResource(getResources(), R.drawable.background), 0, 0);
		loose = new SpriteObject(BitmapFactory.decodeResource(getResources(), R.drawable.loose), 0, 0);	
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		super.surfaceCreated(holder);
		
		loose.setState(SpriteObject.DEAD);
		
		// set win loose position
		loose.setX(super.getGame_width()/2 - (loose.getBitmap().getWidth()/2));
		loose.setY(super.getGame_height()/2 - (loose.getBitmap().getHeight()/2));
		
		mp = MediaPlayer.create(getContext(), R.raw.dodgetheme);
		mp.setLooping(true);
		mp.setVolume(1f, 1f);
		mp.start();
		
		//Populates an array list with 4 different Points
		populateBallPositions();
		
		//Timer to add a ball to an array list every 2 seconds
		new Timer().schedule(new TimerTask() {
			public void run() {
				if(brt.getState() == SpriteObject.ALIVE) {
					if(ballPosIndex == ballPos.size()) {
						ballPosIndex = 0;
					}
					SpriteObject ball = new SpriteObject(BitmapFactory.decodeResource(getResources(), R.drawable.ball), ballPos.get(ballPosIndex).x, ballPos.get(ballPosIndex).y);
					ball.setMoveX(ballVelocityNeg);
					ball.setMoveY(ballVelocityNeg);
					
					ballPosIndex++;
					
					if(balls.size() < 7){
						balls.add(ball);
					}
				} else {
					this.cancel();
				}
			}
		}, 1000, 2000);
		
		//Timer to increment score whilst player state is alive
		new Timer().schedule(new TimerTask() {
			public void run() {
				if(brt.getState() == SpriteObject.ALIVE) {
					score ++;
				} else {
					this.cancel();
				}
			}
		}, 1, 5);
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		super.surfaceDestroyed(holder);
		
		// TODO surface destroyed not working correctly
		
		if(gameEnd == true) {
			mp.stop();
			mp.release();
		}
		else {
			if(mp.isPlaying()) {
				mp.stop();
				mp.release();
			}
		}
	}
	
	@Override
	public void processMotionEvent(InputObject input) {
		if(brt.getState() == SpriteObject.ALIVE) {
			
			// TODO stop player from leaving screen bounds
			brt.setX(input.x);
			brt.setY(input.y);
			
		}
		
		// user selects end game image, return to home screen
		if(loose.getState() == SpriteObject.ALIVE) {
			if(input.eventType == InputObject.ACTION_TOUCH_UP) {
				super.endGame();
			}
		}
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		back.draw(canvas);
		
		for(int i = 0; i < balls.size(); i++) {
			balls.get(i).draw(canvas);
		}
		
		brt.draw(canvas);
		
		trackScore(canvas);
		
		if(loose.getState() == SpriteObject.ALIVE) {
			loose.draw(canvas);
		}
	}
	
	@Override
	public void update(int adj_mov) {
		
		if(brt.getState() == SpriteObject.ALIVE) {
			//Check ball collisions with screen edge
			for(int i = 0; i < balls.size(); i++) {
				if(balls.get(i).getX() >= getWidth()) {
					balls.get(i).setMoveX(ballVelocityNeg);
					playSound(id_ball_noise);
				}
				else if(balls.get(i).getX() <= 0) {
					balls.get(i).setMoveX(ballVelocityPos);
					playSound(id_ball_noise);
				}
				
				if(balls.get(i).getY() >= getHeight()) {
					balls.get(i).setMoveY(ballVelocityNeg);
					playSound(id_ball_noise);
				}
				else if(balls.get(i).getY() <= 0) {
					balls.get(i).setMoveY(ballVelocityPos);
					playSound(id_ball_noise);
				}
			}
			
			//Create a new rectangle with the player's positions
			Rect brtRect = brt.getBounds();
			
			//Create a new rectangle for each ball using each ball's position and check intersection with player character
			for(int i = 0; i < balls.size(); i++) {
				Rect ballRect = balls.get(i).getBounds();
				
				if(brtRect.intersects(ballRect.left, ballRect.top, ballRect.right, ballRect.bottom)) {
					brt.setState(SpriteObject.DEAD);
					
					// set loose screen
					loose.setState(SpriteObject.ALIVE);
					// play loose sound
					if(gameEnd == false) {
						playSound(id_loose_noise);
						gameEnd = true;
					}
				}
			}
			
			for(int i = 0; i < balls.size(); i++) {
				balls.get(i).update(adj_mov);
			}
			
			brt.update(adj_mov);
		} else{
			mp.stop();
		}
		
	}
	
	public void trackScore(Canvas canvas) {
		Paint paint = new Paint();
		
		paint.setColor(Color.BLACK);
		paint.setStyle(Style.FILL);
		
		canvas.drawRect(10, 10, 220, 70, paint);
		
		paint.setColor(Color.WHITE);
		paint.setTextSize(30);
		
		canvas.drawText("Score: " + this.score, 20, 50, paint);
	}
	
	public void populateBallPositions() {
		Point tempPoint;
		
		tempPoint = new Point(100, 100);
		
		ballPos.add(tempPoint);
		
		tempPoint = new Point((super.getGame_width() - 100), 100);
		
		ballPos.add(tempPoint);
		
		tempPoint = new Point((super.getGame_width() - 100), (super.getGame_height() - 100));
		
		ballPos.add(tempPoint);
		
		tempPoint = new Point(100, (super.getGame_height() - 100));
		
		ballPos.add(tempPoint);
	}
}
