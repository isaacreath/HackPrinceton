package com.example.hackprinceton.gui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;
import android.util.TypedValue;

public class Bubble {

	// the outer color of the bubble
	private int color;

	// the radius of a bubble
	private int radius;

	private boolean hasChanged;

	// the current center location
	private float x, y;

	// the velocity
	private float vx, vy;

	private String text;

	private int textSize;

	private float scale;

	// the size of the border in pixels
	private final int BORDER_SIZE = 20;
	private final int FADE_CONSTANT = 250;

	public Bubble(float locx, float locy,float vy,String t) {
		x = locx;
		y = locy;

		vx = 0;
		this.vy = vy * -1;
		
		color = Color.GREEN;

		radius = 200;

		text = t;

		hasChanged = true;

		scale = 0.2f;

		textSize = 40;

		TextPaint mTextPaint = new TextPaint();
		
		Rect rec = new Rect((int) x - radius / 2, (int) y - radius / 2, (int) x
				+ radius / 2, (int) y + radius / 2);
		Rect rec2 = new Rect();

		do {
			mTextPaint.getTextBounds(text, 0, text.length(), rec2);
			textSize--;
			mTextPaint.setTextSize(textSize);

		} while (rec2.bottom - rec2.top > rec.bottom - rec.top);

		do {
			mTextPaint.getTextBounds(text, 0, text.length(), rec2);
			scale -= 0.1f;
			mTextPaint.setTextScaleX(scale);
		} while (rec2.right - rec2.left > rec.right - rec.left);

		

	}

	// updates the bubble * the num of ticks for smooth rendering
	public void update(int ticks) {
		x += vx * ticks;
		y += vy * ticks;
	}

	public void draw(Canvas c) {
		Paint p = new Paint();

		// create the first circle with a border

		p.setColor(color);
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(BORDER_SIZE);

		p.setAntiAlias(true);

		// p.setColorFilter(ColorFilter.)
		c.drawCircle(x, y, radius, p);

		// create next circle with
		int r, g, b, a; // these ints will hold each color component to mutilate

		r = Color.red(color);
		g = Color.green(color);
		b = Color.blue(color);
		a = Color.alpha(color);

		p.reset();

		int newColor = Color.argb(a / 2, r, g, b);
		TextPaint mTextPaint = new TextPaint();

		mTextPaint.setTextSize(textSize);
		Rect rec = new Rect((int) x - radius / 2, (int) y - radius / 4, (int) x
				+ radius / 2, (int) y + radius / 4);
		Rect rec2 = new Rect();

		mTextPaint.getTextBounds(text, 0, text.length(), rec2);

//		float lRatio = (rec.bottom - rec.top) / (rec2.bottom - rec2.top);
//		float wRatio = (rec.right - rec.left) / (rec2.right - rec2.left);
//		
//
//		mTextPaint.setTextSize(textSize*lRatio);
//		mTextPaint.setTextScaleX(scale*wRatio);

		/*
		 * if (hasChanged) { scale = 2;
		 * 
		 * do { mTextPaint.getTextBounds(text, 0, text.length(), rec2);
		 * 
		 * textSize--; mTextPaint.setTextSize(textSize);
		 * 
		 * } while (rec2.bottom - rec2.top > rec.bottom - rec.top);
		 * 
		 * do { mTextPaint.getTextBounds(text, 0, text.length(), rec2); scale -=
		 * 0.01f; mTextPaint.setTextScaleX(scale); } while (rec2.right -
		 * rec2.left > rec.right - rec.left); }
		 * 
		 * mTextPaint.setTextSize(textSize); mTextPaint.setTextScaleX(scale);
		 */

		c.drawText("a: " + a + " red: " + r + " green: " + g + " blue: " + b,
				60, 60, p);

		p.setColor(newColor);
		p.setStyle(Paint.Style.FILL_AND_STROKE);
		c.drawCircle(x, y, radius, p);

		c.drawText(text, x - radius / 2, y, mTextPaint);

		hasChanged = false;

	}

	public void grow() {
		hasChanged = false;
		radius += 10;
		
	}

	public void shrink() {
		hasChanged = true;
		radius -= 5;
	}

	public int getRadius() {
		return radius;
	}
	
	public void setV(float vx, float vy){
		this.vx = vx;
		this.vy = vy;
	}

}
