package com.example.hackprinceton.gui;

import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.util.TypedValue;

public class Bubble {
	// the outer color of the bubble
	private int color;
	static int cases = 1;
	// the radius of a bubble
	private int radius;

	private boolean hasChanged;

	// the current center location
	private float x, y;

	// the velocity
	private float vx, vy;

	private String text;
	
	private int alpha;

	private int textSize;

	private float scale;
	
	private boolean isVisible;
	
	private TextPaint tp;
	private Paint p;

	// the size of the border in pixels
	private final int BORDER_SIZE = 5;
	private final int FADE_CONSTANT = 250;
	private float startX, startY;
	private final double pct = .15;
	private Rect rec,rec2;
	private StaticLayout sl;
	public Bubble(float locx, float locy,float vy,String t) {
		x = locx;
		y = locy;
		startX = x;
		startY = y;
		vx = 0;
		this.vy = vy * -1;
		
		setColor();
		
		alpha = 255;

		radius = 200;

		text = t;

		hasChanged = true;

		scale = 0.2f;
		textSize = (int) (radius* pct);

		tp = new TextPaint();
		
		tp.setTypeface(Typeface.create("Helvetica", Typeface.NORMAL));
		
		rec = new Rect((int) x - radius / 2, (int) y - radius / 2, (int) x
				+ radius / 2, (int) y + radius / 2);
		rec2 = new Rect();

		do {
			tp.getTextBounds(text, 0, text.length(), rec2);
			textSize--;
			tp.setTextSize(textSize);

		} while (rec2.bottom - rec2.top > rec.bottom - rec.top);

		int f;
		
		do {
			tp.setTextSize(textSize);
			
			f = (int) StaticLayout.getDesiredWidth(text, tp);
			
			if(f > radius*2)
				scale -= 0.2;

		} while (f > radius*2);

		isVisible = true;
		
		p = new Paint();
		

	}
	
	public boolean isVisible(){
		return isVisible;
	}
	
	public void setVisible(boolean b){
		isVisible = b;
	}

	public void setColor(){
		switch(cases){
		case 1:
			color = Color.RED;
			cases++;
			break;
		case 2:
			color = Color.BLUE;
			cases++;
			break;
		
		case 3:
			color = Color.GREEN;
			cases = 1;
			break;
		}
	}
	// updates the bubble * the num of ticks for smooth rendering
	public void update(int ticks) {
		x += vx * ticks;
		y += vy * ticks;
	}

	public void draw(Canvas c) {

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


		int newColor = Color.argb(a / 2, r, g, b);

		tp.setTextSize((float) (radius * pct));

		




		p.setColor(newColor);
		p.setStyle(Paint.Style.FILL_AND_STROKE);
		c.drawCircle(x, y, radius, p);
		
		tp.setAlpha(alpha);
		
		if(text != null){
			c.save();
		
			c.translate(x-radius, y-radius/4);
			sl = new StaticLayout(text, tp, radius*2, Alignment.ALIGN_CENTER, 1.0f, 1.0f, false);
			
			sl.draw(c);
		
			c.restore();
		}
	
		

		

		hasChanged = false;

	}

	public void grow() {
		hasChanged = false;
		radius += 10;
		textSize = (int) (radius* pct);

		if(alpha - 5 >= 0){
			alpha -= 5;
		}
		else if(alpha > 0){
			alpha -= alpha;
		}
	}
	
	public void setloc(float x, float y){
		this.x = x;
		this.y = y;
	}
	
	public void grow(int a) {
		hasChanged = true;
		radius += 10;
		textSize = (int) (radius* pct);

		if(alpha + 5 <= 255){
			alpha += 5;
		}
		else{
			alpha = 255;
		}
	}
	
	public void growSize(int s, int a){
		hasChanged = true;
		radius += s;
		textSize = (int) (radius* pct);
		
		if(alpha + a <= 255 && alpha + a >= 0){
			alpha += a;
		}
		else{
			alpha = a < 0 ? 0 : 255; 
		}
	}

	public void shrink(int r) {
		hasChanged = true;
		if(radius - 10 <= r){
			radius = r;
		}
		else{
			radius -= 10;
		}
		
		textSize = (int) (radius* pct);
		
		
		if(alpha + 25 <= 255){
			alpha += 25;
		}
		else
			alpha += 255 - alpha;

	}
	
	public void shrink(int r, int a){
		hasChanged = true;
		if(radius - 10 <= r){
			radius = r;
		}
		else{
			radius -= 10;
		}
		
		textSize = (int) (radius* pct);
		
		
		if(alpha + a >= 0){
			alpha += a;
		}
		else
			alpha = 0;
	}
	
	public void setRadius (int r){
		radius = r;
		textSize = (int) (radius * pct);
	}

	public float getX(){
		return x;
	}
	public float getY(){
		return y;
	}
	public int getRadius() {
		return radius;
	}
	public void floatBehavior(){

		if(y > startY + 40){
			vy *= -1;
		}
		if(y < startY - 40){
			vy *= -1;
		}
		
	}
	
	public void setStartCoords(float x, float y){
		this.startX = x;
		this.startY = y;
	}
	public void setVx(float vx){
		this.vx = vx;
	}
	
	public void setVy(float vy){
		this.vy = vy;
	}
	public String getText(){
		return text;
	}
	public void setAlpha(int val){
		alpha = val;
	}
	public void setText(String t){
		this.text = t;
	}
	public void growAlpha(int rate){
		alpha = alpha + rate;
	}

}
