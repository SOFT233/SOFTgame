package com.soft.softgame;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.media.MediaPlayer;
import android.view.SurfaceHolder;

import com.soft.model.SpriteObject;
import com.soft.util.GameView;
import com.soft.util.InputObject;

public class PongView extends GameView {
	
	private SpriteObject brt;
	private SpriteObject bry;
	private SpriteObject ball;
	private SpriteObject back;
	private SpriteObject win;
	private SpriteObject loose;
	private int id_ball_noise;
	private int id_win_noise;
	private int id_loose_noise;
	private boolean gameEnd = false;
	private boolean roundStarted = false;
	private double ballVelocity = 0.5;
	private double computerPlayerVelocity = 0.6;
	private int brtScore = 0;
	private int bryScore = 0;
	private int scoreLimit = 5;
	private MediaPlayer mp;

	public PongView(Context context) {
		super(context);
		
		id_ball_noise = this.getSoundPool().load(context, R.raw.ball, 1);
		id_win_noise = this.getSoundPool().load(context, R.raw.win, 1);
		id_loose_noise = this.getSoundPool().load(context, R.raw.loose, 1);
		
		brt = new SpriteObject(BitmapFactory.decodeResource(getResources(), R.drawable.brt), 0, 0);
		bry = new SpriteObject(BitmapFactory.decodeResource(getResources(), R.drawable.bry), 0, 0);
		ball = new SpriteObject(BitmapFactory.decodeResource(getResources(), R.drawable.ball), 0, 0);
		back = new SpriteObject(BitmapFactory.decodeResource(getResources(), R.drawable.background), 0, 0);
		win = new SpriteObject(BitmapFactory.decodeResource(getResources(), R.drawable.win), 0, 0);
		loose = new SpriteObject(BitmapFactory.decodeResource(getResources(), R.drawable.loose), 0, 0);
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		super.surfaceCreated(holder);
		
		win.setState(SpriteObject.DEAD);
		loose.setState(SpriteObject.DEAD);
		
		// set win loose position
		win.setX(getGame_width()/2 - (win.getBitmap().getWidth()/2));
		win.setY(getGame_height()/2 - (win.getBitmap().getHeight()/2));
		loose.setX(getGame_width()/2 - (loose.getBitmap().getWidth()/2));
		loose.setY(getGame_height()/2 - (loose.getBitmap().getHeight()/2));
		
		newRound(1);
		
		// run background music loop
		mp = MediaPlayer.create(getContext(), R.raw.pongtheme);
		mp.setLooping(true);
		mp.setVolume(1f, 1f);
		mp.start();
		
		
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
	
	public void processMotionEvent(InputObject input) {
		
		if(brt.getState() == SpriteObject.ALIVE && bry.getState() == SpriteObject.ALIVE) {
			if(input.x > getGame_width()/2) {
				// TODO stop player from leaving screen bounds
				brt.setY(input.y);
				
				
				// TODO attempt to make player move with velocity
//				playerMovement(brt,
//								new Point(input.x, input.y),
//								1,
//								false,
//								true);
			}
			
			if(ball.getMoveX() == 0 && ball.getMoveY() == 0) {
				if(ball.getX() > getGame_width()/2) {
					ball.setMoveX(-ballVelocity);
					ball.setMoveY(-ballVelocity);
				}
				else {
					ball.setMoveX(ballVelocity);
					ball.setMoveY(ballVelocity);
				}
			}
		}
		
		// user selects end game image, return to home screen
		if(win.getState() == SpriteObject.ALIVE || loose.getState() == SpriteObject.ALIVE) {
			if(input.eventType == InputObject.ACTION_TOUCH_UP) {
				endGame();
			}
		}
		
		// TODO fix player 2 motion
//		if(bry.getState() == SpriteObject.ALIVE) {
//			if(input.x < getGame_width()/2) {
//				bry.setY(input.y);
//			}
//		}
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		back.draw(canvas);
		
		ball.draw(canvas);
		brt.draw(canvas);
		bry.draw(canvas);
		
		trackScore(canvas);
		
		if(win.getState() == SpriteObject.ALIVE) {
			win.draw(canvas);
		}
		
		if(loose.getState() == SpriteObject.ALIVE) {
			loose.draw(canvas);
		}
	}
	
	public void update(int adj_mov) {
		// check sprite state
		if(brt.getState() == SpriteObject.DEAD) {
			
			ball.setMoveX(0);
			ball.setMoveY(0);
			
			// stop the music
			mp.stop();
			
			// set loose screen
			loose.setState(SpriteObject.ALIVE);
			// play loose sound
			if(gameEnd == false) {
				playSound(id_loose_noise);
				gameEnd = true;
			}
			// display bry wins
		}
		else if(bry.getState() == SpriteObject.DEAD) {
			
			ball.setMoveX(0);
			ball.setMoveY(0);
			
			// stop the music
			mp.stop();
			
			// set win screen
			win.setState(SpriteObject.ALIVE);
			// play win sound
			if(gameEnd == false) {
				playSound(id_win_noise);
				gameEnd = true;
			}
			// display brt wins
		}
		else {
			// check for object collisions
			collisionChecks();
			
			// movement for computer player
			if(ball.getY() != 0 && ball.getX() < getGame_width()/2) {
				playerMovement(bry, 
								new Point((int)(ball.getX()), 
											(int)(ball.getY() + ball.getBitmap().getHeight()/2)),
								computerPlayerVelocity,
								false,
								true);
			}
			else {
				bry.setMoveY(0);
			}
			
			// perform specific updates
			ball.update(adj_mov);
			brt.update(adj_mov);
			bry.update(adj_mov);
		}
	}
	
	public void trackScore(Canvas canvas) {
		Paint paint = new Paint();
		
		paint.setColor(Color.BLACK);
		paint.setStyle(Style.FILL);
		
		canvas.drawRect(10, 10, 220, 70, paint);
		
		paint.setColor(Color.WHITE);
		paint.setTextSize(30);
		
		canvas.drawText("Score: " + this.bryScore +" - "+ this.brtScore, 20, 50, paint);
	}

	public void playerMovement(SpriteObject player, Point target, double velocity, boolean xEnabled, boolean yEnabled) {
		double pointY = target.y;
		double playerY = player.getY() + player.getBitmap().getHeight()/2;
		double pointX = target.x;
		double playerX = player.getX() + player.getBitmap().getWidth()/2;
		
		// if there is a difference in position
		if((playerY - pointY != 0) && (playerX - pointX != 0)) {
			// track y coord of point
			if((playerY < pointY) && yEnabled) {
				// stop player from going off bottom of screen
				if(player.getY() + player.getBitmap().getHeight() >= getGame_height()) {
					player.setMoveY(0);
					player.setY(getGame_height() - player.getBitmap().getHeight());
				}
				else {
					player.setMoveY(velocity);
				}
			}
			else if((playerY > pointY) && yEnabled) {
				// stop player from going off top of screen
				if(player.getY() <= 0) {
					player.setMoveY(0);
					player.setY(0);
				}
				else {
					player.setMoveY(-velocity);
				}
			}
			
			// track x coord of point
			if((playerX < pointX) && xEnabled) {
				// stop player from going off bottom of screen
				if(player.getX() + player.getBitmap().getWidth() >= getGame_width()) {
					player.setMoveX(0);
					player.setX(getGame_width() - player.getBitmap().getWidth());
				}
				else {
					player.setMoveX(velocity);
				}
			}
			else if((playerX > pointX) && xEnabled) {
				// stop player from going off top of screen
				if(player.getX() <= 0) {
					player.setMoveX(0);
					player.setX(0);
				}
				else {
					player.setMoveX(-velocity);
				}
			}
		}
		else {
			// stop the player
			player.setMoveY(0);
			player.setMoveX(0);
		}
	}
	
	public void collisionChecks() {
		// collisions with walls
		if(ball.getX() >= getGame_width()) {
			if(bryScore == scoreLimit) {
				// if it goes off the back of the screen increment score or set to dead
				brt.setState(SpriteObject.DEAD);
				roundStarted = false;
			}
			else {
				bryScore++;
				newRound(2);
			}
			
			// for debug
			//ball.setMoveY(ballVelocityNeg);
		}
		else if(ball.getX() <= 0) {
			if(brtScore == scoreLimit) {
				// if it goes off the back of the screen set to dead
				bry.setState(SpriteObject.DEAD);
				roundStarted = false;
			}
			else {
				brtScore++;
				newRound(1);
			}
			
			// for debug
			//ball.setMoveY(ballVelocityPos);
		}
		
		if(ball.getY() >= getGame_height()) {
			ball.setMoveY(-ballVelocity);
			playSound(id_ball_noise);
		}
		else if(ball.getY() <= 0) {
			ball.setMoveY(ballVelocity);
			playSound(id_ball_noise);
		}
		
		// get ball and player hit boxes
		Rect brtRect = brt.getBounds();
		Rect bryRect = bry.getBounds();
		Rect ballRect = ball.getBounds();
		
		// check player ball collisions
		if(brtRect.intersects(ballRect.left, ballRect.top, ballRect.right, ballRect.bottom)) {
			ball.setMoveX(-ballVelocity);
			playSound(id_ball_noise);
			//Log.i("collision:Brt", "hit by ball");
		}
		
		if(bryRect.intersects(ballRect.left, ballRect.top, ballRect.right, ballRect.bottom)) {
			ball.setMoveX(ballVelocity);
			playSound(id_ball_noise);
			//Log.i("collision:Bry", "hit by ball");
		}
	}
	
	public void newRound(int ballHolder) {
		
		// reset velocity;
		ball.setMoveX(0);
		ball.setMoveY(0);
		ballVelocity = 0.5;
		
		// centre to screen
		brt.setX(getGame_width() - 130);
		brt.setY(getGame_height()/2 - (brt.getBitmap().getHeight()/2));
		
		bry.setX(130 - bry.getBitmap().getWidth());
		bry.setY(brt.getY());
		
		if(ballHolder == 1) {
			// centre to brt
			ball.setX(brt.getX() - 50);
			ball.setY(brt.getY() + ((brt.getBitmap().getHeight()/2) - (ball.getBitmap().getHeight()/2)));
		}
		else {
			// centre to brt
			ball.setX(bry.getX() + bry.getBitmap().getWidth() + 50);
			ball.setY(bry.getY() + ((bry.getBitmap().getHeight()/2) - (ball.getBitmap().getHeight()/2)));
		}
		
		//Timer to increment ball speed whilst player state is alive
		if(roundStarted == false) {
			new Timer().schedule(new TimerTask() {
				public void run() {
					if(roundStarted) {
						ballVelocity += 0.01;
						
					} else {
						this.cancel();
					}
				}
			}, 1, 2000);
		}
		
		// start round
		roundStarted = true;
	}
}
