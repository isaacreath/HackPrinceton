package com.example.hackprinceton.gui;

//import com.example.hackprinceton.backend.Requests;

import java.util.ArrayList;
import java.util.Random;

import com.example.hackprinceton.backend.Requests;

//import com.example.hackprinceton.backend.Requests;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;

public class RenderActivity extends Activity {
	private RenderView rv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		rv = new RenderView(this);
		setContentView(rv);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		rv.resume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		rv.pause();
	}

	private class RenderView extends SurfaceView implements Runnable,
			OnTouchListener {
		private class InnerThread implements Runnable {
			String selectedHashtag;

			public InnerThread(String fileName) {
				selectedHashtag = fileName;
			}

			@Override
			public void run() {
				tweets = Requests.getTweet(selectedHashtag);
			}

		}

		private Thread t;
		private boolean hasGottenTweets, startedThreads;
		private SurfaceHolder holder;
		private Thread renderThread;
		private int index;
		private ArrayList<String> tweets;
		// private com.example.hackprinceton.backend.Requests r;

		private ArrayList<Bubble> b;
		private ArrayList<Bubble> bubbles;
		private int state;
		private Random r;
		private volatile boolean running;

		private WakeLock wl;
		private int mActivePointerId;
		private float mLastTouchX;
		private float mPosY;
		private float mLastTouchY;
		private float mPosX;
		private Bubble cur;

		public RenderView(Context context) {
			super(context);
			r = new Random();
			hasGottenTweets = false;
			setOnTouchListener(this);
			renderThread = null;
			holder = getHolder();
			running = false;
			bubbles = new ArrayList<Bubble>();
			state = 0;
			bubbles.add(new Bubble(1000, 500, 1, ""));
			bubbles.add(new Bubble(1000, 1500, 1, ""));
			bubbles.add(new Bubble(800, 500, 1, ""));
			bubbles.add(new Bubble(200, 500, 1, ""));
			bubbles.add(new Bubble(1000, 800, 1, ""));
			bubbles.add(new Bubble(1000, 700, 1, ""));
			bubbles.add(new Bubble(500, 1000, 1, ""));
			bubbles.add(new Bubble(800, 1000, 1, ""));
			bubbles.add(new Bubble(600, 550, 1, ""));
			bubbles.add(new Bubble(1200, 600, 1, ""));
			// bubbles.add(new Bubble(1500, 300, 1, ""));
			// bubbles.add(new Bubble(1200, 200, 1, ""));
			// bubbles.add(new Bubble(1600, 900, 1, ""));
			// bubbles.add(new Bubble(1500, 500, 1, ""));
			// bubbles.add(new Bubble(900, 1200, 1, ""));
			for (Bubble b : bubbles) {
				b.setRadius(1);
				b.setAlpha(0);
				b.setVisible(false);
			}

			b = new ArrayList<Bubble>();

			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");

		}

		public void resume() {
			running = true;
			renderThread = new Thread(this);
			renderThread.start();

			wl.acquire();
		}

		public void pause() {
			running = false;
			wl.release();

			while (true) {
				try {
					renderThread.join();
					return;
				} catch (InterruptedException e) {
					// too bad
				}
			}
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			ArrayList<String> hashtags = null;
			long currentTime = System.nanoTime();
			while (running) {
				currentTime = System.nanoTime();

				if (!holder.getSurface().isValid())
					continue;

				Canvas canvas = holder.lockCanvas();
				;

				switch (state) {
				case 0:
					hashtags = Requests.getTrending();

					for (int i = 0; i < 10; i++) {
						Random r = new Random(System.nanoTime());
						b.add(new Bubble(r.nextFloat() * (getWidth() - 200)
								+ 200, r.nextFloat() * getHeight()
								+ getHeight(), (float) (r.nextFloat()),
								hashtags.get(i)));
					}
					state = 1;
					break;
				case 1:
					canvas.drawColor(Color.RED - 50);
					startedThreads = false;

					// update
					for (Bubble bub : b) {
						bub.update(1);
						bub.draw(canvas);
					}

					break;
				case 2:
					canvas.drawColor(Color.RED - 50);

					Bubble bubble = b.get(index);
					String selectedHashtag = bubble.getText();

					if (!startedThreads) {
						for (int i = 0; i < bubbles.size(); i++) {
							Bubble temp = bubbles.get(i);
							int rx = r.nextInt(300);
							int ry = r.nextInt(300);
							int swap = r.nextInt(2);
							if (swap == 2) {
								swap = -1;
							}

							bubbles.get(i).setloc(bubble.getX() + rx * swap,
									bubble.getY() + ry * swap);
							if (temp.getRadius() < 200) {
								int vy = r.nextInt(10) + 2;
								if (temp.getY() <= getHeight() / 2) {

									temp.setVy(vy);
								} else {
									temp.setVy(-1 * vy);
								}

								int vx = r.nextInt(10) + 2;
								if (temp.getX() >= getWidth() / 2) {
									temp.setVx(-1 * vx);
								} else {
									temp.setVx(vx);
								}
							}
						}
						for (int i = 0; i < b.size(); i++) {
							Bubble temp = b.get(i);

							if (i == index)
								continue;

							if (temp.getX() < bubble.getX()) {
								temp.setVx(-10);
							} else
								temp.setVx(10);
						}

						for (int i = 0; i < bubbles.size(); i++) {
							Bubble temp = bubbles.get(i);
							temp.setVisible(true);
						}

						t = new Thread(new InnerThread(selectedHashtag));
						t.start();
						startedThreads = true;
					}

					// grow the main bubble
					if (bubble.getX() - bubble.getRadius() > -200
							|| bubble.getX() + bubble.getRadius() < getWidth() + 200
							|| bubble.getY() - bubble.getRadius() > -200
							|| bubble.getY() + bubble.getRadius() < getHeight() + 200) {
						bubble.growSize(20, -10);
						bubble.update(1);
						bubble.draw(canvas);
					} else {
						for (int i = 0; i < b.size(); i++) {
							Bubble temp = b.get(i);

							if (i == index)
								continue;

							temp.setVx(0);
						}
						state = 3;
					}

					// move the other bubbles
					Bubble bs;
					for (int i = 0; i < b.size(); i++) {
						if (i == index)
							continue;

						bs = b.get(i);
						bs.update(1);
						bs.draw(canvas);
					}
					Bubble temp;

					// make the subbubbles grow
					for (int i = 0; i < bubbles.size(); i++) {
						temp = bubbles.get(i);

						temp.growSize(1, 10);
						temp.update(1);
						temp.setText(null);

						temp.draw(canvas);
					}

					break;
				case 3:
					canvas.drawColor(Color.RED - 50);
					while (t != null) {
						try {
							t.join();
							t = null;
							break;
						} catch (Exception e) {
							Log.e("FUCKFUCKFUCKFUCK", e.getMessage(), null);
						}
					}
					Integer tweetSize = tweets.size();

					Integer sizes = bubbles.size();

					b.get(index).draw(canvas);
					Bubble temp1;
					for (int i = 0; i < tweets.size() - 5 && !hasGottenTweets; i++) {
						temp = bubbles.get(i);
						temp.setText(tweets.get(i));
						temp.setAlpha(255);
						temp.setStartCoords(temp.getX(), temp.getY());
						temp.setRadius(200);
						temp.setVx(0);
						temp.setVy(r.nextInt(2) + 1);
					}

					hasGottenTweets = true;
					for (int i = 0; i < bubbles.size(); i++) {
						temp = bubbles.get(i);

						temp.update(1);
						temp.floatBehavior();
						temp.draw(canvas);
					}

					break;

				case 4:
					canvas.drawColor(Color.RED - 50);

					Bubble bubble1 = b.get(index);
					hasGottenTweets = false;

					// shrink the main bubble
					if (bubble1.getRadius() > 200) {
						bubble1.shrink(200);
						bubble1.update(1);
					} else {
						state = 1;
					}

					bubble1.draw(canvas);

					// shrink the smaller bubbles
					for (int i = 0; i < bubbles.size()
							&& bubbles.get(bubbles.size() - 1).getRadius() > 5; i++) {
						temp = bubbles.get(i);

						if (temp.getRadius() > 5) {
							temp.shrink(5, -10);
							temp.setText(null);
							temp.update(1);
							temp.draw(canvas);
						} else {
							temp.setVisible(false);
						}
					}

					// move the other bubbles back
					for (int i = 0; i < b.size(); i++) {
						if (i == index)
							continue;

						if (b.get(i).getX() < bubble1.getX()) {
							b.get(i).setVx(10);
						} else
							b.get(i).setVx(-10);

						b.get(i).update(1);
						b.get(i).draw(canvas);
					}
					for (int i = 0; i < b.size(); i++) {
						b.get(i).setVx(0);
					}

					break;

				}
				// create the bubbles
				holder.unlockCanvasAndPost(canvas);

				currentTime = System.nanoTime() - currentTime;
				try {
					long x = (1000 / 60) - (currentTime / 1000000);
					if (x > 0) {
						Thread.sleep(x);
					} else {
						Thread.sleep(1000 / 60);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

		// private boolean isCollision(float p1x, float p1y, float r1, float
		// p2x,
		// float p2y, float r2) {
		// final double a = r1 + r2;
		// final double dx = p1x - p2x;
		// final double dy = p1y - p2y;
		// return a * a > (dx * dx + dy * dy);
		// }

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			if (state == 1) {
				for (int i = 0; i < b.size(); i++) {
					Bubble x = b.get(i);

					if (event.getAction() == MotionEvent.ACTION_DOWN
							&& (event.getX() - x.getX())
									* (event.getX() - x.getX())
									+ (event.getY() - x.getY())
									* (event.getY() - x.getY()) < b.get(0)
									.getRadius() * x.getRadius()) {
						index = i;
						state = 2;
					}

				}
			} else if (state == 3) {
				final int action = MotionEventCompat.getActionMasked(event);

				switch (action) {
				case MotionEvent.ACTION_DOWN: {
					for(int i = 0; i < bubbles.size();i++){
						if ((event.getX() - bubbles.get(i).getX())
										* (event.getX() - bubbles.get(i).getX())
										+ (event.getY() - bubbles.get(i).getY())
										* (event.getY() - bubbles.get(i).getY()) < bubbles.get(i).getRadius() 
										* bubbles.get(i).getRadius()) 
						{
							cur = bubbles.get(i);
						}
					}
					if(cur == null){
						state = 4;
						break;
					}
					final int pointerIndex = MotionEventCompat
							.getActionIndex(event);
					final float x = MotionEventCompat.getX(event, pointerIndex);
					final float y = MotionEventCompat.getY(event, pointerIndex);

					// Remember where we started (for dragging)
					mLastTouchX = cur.getX();
					mLastTouchY = cur.getY();
					// Save the ID of this pointer (for dragging)
					mActivePointerId = MotionEventCompat.getPointerId(event, 0);
					break;
				}

				case MotionEvent.ACTION_MOVE: {
					// Find the index of the active pointer and fetch its
					// position
					final int pointerIndex = MotionEventCompat
							.findPointerIndex(event, mActivePointerId);

					final float x = MotionEventCompat.getX(event, pointerIndex);
					final float y = MotionEventCompat.getY(event, pointerIndex);

					// Calculate the distance moved
					final float dx = x - mLastTouchX;
					final float dy = y - mLastTouchY;

					mPosX += dx;
					mPosY += dy;
					cur.setloc(x, y);
					cur.setStartCoords(x, y);
					invalidate();

					// Remember this touch position for the next move event
					mLastTouchX = x;
					mLastTouchY = y;

					break;
				}

				case MotionEvent.ACTION_UP: {
					cur = null;
					mActivePointerId = MotionEvent.INVALID_POINTER_ID;
					break;
				}

				case MotionEvent.ACTION_CANCEL: {
					cur = null;
					mActivePointerId = MotionEvent.INVALID_POINTER_ID;
					break;
				}

				case MotionEvent.ACTION_POINTER_UP: {

					final int pointerIndex = MotionEventCompat
							.getActionIndex(event);
					final int pointerId = MotionEventCompat.getPointerId(event,
							pointerIndex);

					if (pointerId == mActivePointerId) {
						// This was our active pointer going up. Choose a new
						// active pointer and adjust accordingly.
						final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
						mLastTouchX = MotionEventCompat.getX(event,
								newPointerIndex);
						mLastTouchY = MotionEventCompat.getY(event,
								newPointerIndex);
						mActivePointerId = MotionEventCompat.getPointerId(
								event, newPointerIndex);
					}
					cur = null;
					break;
				}
				}

			}

			return true;
		}
	}
}
