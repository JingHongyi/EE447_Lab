package com.android.DrawLineSample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

class TestView extends View {

	public Canvas canvas;
	public Paint p;
	private Bitmap bitmap;
	public Point point;
	public WindowManager wm;
	private ImageView ima;
	int colorindex = 0;
	int screenwidth;
	int screenheight;
	float x,y;
	int bgColor;
	public TestView(Context context) {
		super(context);
		              //设置背景颜色
		wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getSize(point);
		screenwidth = point.x;
		screenheight = point.y;
		bitmap = Bitmap.createBitmap(screenwidth,200, Bitmap.Config.ARGB_8888);    //设置位图，线就画在位图上面，第一二个参数是位图宽和高
		/*for(int i=0;i<bitmap.getWidth();i++){
			for(int j=0;j<bitmap.getHeight();j++){
				bitmap.setPixel(i,j,Color.BLACK);//将bitmap的每个像素点都设置成相应的颜色
			}
		}
		*/
        String[] color = new String[5];
        color[0] = "BLUE";
		color[1] = "RED";
		color[2] = "GREEN";
		color[3] = "YELLOW";
		color[4] = "BLACK";
        ima=(ImageView) findViewById(R.id.jiaoda);
        bitmap = Bitmap.createBitmap(ima.getWidth(),ima.getHeight(),Bitmap.Config.ARGB_8888);
        ima.setDrawingCacheEnabled(true);
        bitmap=ima.getDrawingCache();
        canvas=new Canvas(bitmap);
        canvas.drawBitmap(bitmap, 0, 0, p);
		canvas=new Canvas();         
		canvas.setBitmap(bitmap);       
		p = new Paint(Paint.DITHER_FLAG);
		p.setAntiAlias(true);                //设置抗锯齿，一般设为true
		p.setColor(Color.WHITE);
		switch (colorindex){
			case 0:p.setColor(Color.BLUE);break;
			case 1:p.setColor(Color.BLACK);break;
			case 2:p.setColor(Color.RED);break;
			case 3:p.setColor(Color.YELLOW);break;
			case 4:p.setColor(Color.GREEN);break;
		}

       //设置线的颜色
		p.setStrokeCap(Paint.Cap.SQUARE);     //设置线的类型
		p.setStrokeWidth(10);                //设置线的宽度
		
	}
    
	
	//触摸事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_MOVE) {    //拖动屏幕
			canvas.drawLine(x, y, event.getX(), event.getY(), p);   //画线，x，y是上次的坐标，event.getX(), event.getY()是当前坐标
			invalidate();
		}

		if (event.getAction() == MotionEvent.ACTION_DOWN) {    //按下屏幕
			x = event.getX();				
			y = event.getY();
			canvas.drawPoint(x, y, p);                //画点
			invalidate();
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {    //松开屏幕
            colorindex = (colorindex+1)%5;
		}
		x = event.getX();   //记录坐标
		y = event.getY();

		switch (colorindex){
			case 0:p.setColor(Color.BLUE);break;
			case 1:p.setColor(Color.BLACK);break;
			case 2:p.setColor(Color.RED);break;
			case 3:p.setColor(Color.YELLOW);break;
			case 4:p.setColor(Color.GREEN);break;

		}

		return true;
	}
	
	@Override
	public void onDraw(Canvas c) {			    		
		c.drawBitmap(bitmap, 0, 0, null);
	}
 }
