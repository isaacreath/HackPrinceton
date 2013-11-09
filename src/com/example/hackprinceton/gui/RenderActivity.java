package com.example.hackprinceton.gui;

//import com.example.hackprinceton.backend.Requests;

import java.util.ArrayList;
import java.util.Random;

//import com.example.hackprinceton.backend.Requests;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;

public class RenderActivity extends Activity{
	private RenderView rv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
	
	private class RenderView extends SurfaceView implements Runnable, OnTouchListener{
	
		private SurfaceHolder holder;
		private Thread renderThread;
		//private com.example.hackprinceton.backend.Requests r;
		
		//TEMP
		private String s;
		
		private ArrayList<Bubble> b;
		
		private int state,lastState;
		
		private volatile boolean running;
		
		public RenderView(Context context) {
			super(context);
			
			renderThread = null;
			holder = getHolder();
			running = false;
			
			state = 0; lastState = 0;
			
			b = new ArrayList<Bubble>();		
			
		}
		
		public void resume(){
			running = true;
			renderThread = new Thread(this);
			renderThread.start();
		}
		
		public void pause() {
			running = false;

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
			while (running) {
				if (!holder.getSurface().isValid())
					continue;

				
				lastState = state;
				state = 1;
				Canvas canvas = holder.lockCanvas();
				canvas.drawColor(Color.WHITE);
				
				//update
				for(Bubble bub : b){
					bub.update(1);
					bub.draw(canvas);
				}
	

				
				if(state != lastState){
				//	s = Requests.makeRequests();
				}
				
				if(s != null){
					canvas.drawText(s, 100f, 100f, new Paint());
				}
				
				
				holder.unlockCanvasAndPost(canvas);
				
				try {
					Thread.sleep(17);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			return false;
		}
	}
}
