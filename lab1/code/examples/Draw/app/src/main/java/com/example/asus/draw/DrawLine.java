package com.example.asus.draw;

import android.support.v7.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class DrawLine extends AppCompatActivity {
    private Canvas canvas;
    private Paint paint;
    private Bitmap bitmap;
    private ImageView ima;
    float x,y;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ima = (ImageView) findViewById(R.id.ima);
        //get the size of the screen pixels
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int mScreenWidth,mScreenHeight;
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        //define the bitmap and the canvas,the paint
        bitmap = Bitmap.createBitmap(mScreenWidth+100, mScreenHeight, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);//initialize the background color
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);//抗锯齿
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(8);
        ima.setImageBitmap(bitmap);
        //set the touching event using the Listener function
        ima.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_MOVE){
                    canvas.drawLine(x,y,event.getX(),event.getY(), paint);
                    ima.setImageBitmap(bitmap);
                }
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    x = event.getX();   //Record the coordinate
                    y = event.getY();
                    canvas.drawPoint(x,y,paint);
                    ima.setImageBitmap(bitmap);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                }
                x = event.getX();   //Record the coordinate
                y = event.getY();
                return true;
            }
        });
    }
    //create all the button touching events to change the paint color
    public void ButtonClick(View v) {
        //获取Button组件的资源id
        int id = v.getId();
        switch (id) {
            case R.id.RED:
                paint.setStrokeWidth(8);
                paint.setColor(Color.RED);
                break;
            case R.id.BLUE:
                paint.setStrokeWidth(8);
                paint.setColor(Color.BLUE);
                break;
            case R.id.GREEN:
                paint.setStrokeWidth(8);
                paint.setColor(Color.GREEN);
                break;
            case R.id.YELLOW:
                paint.setStrokeWidth(8);
                paint.setColor(Color.YELLOW);
                break;
            case R.id.PURPLE:
                paint.setStrokeWidth(8);
                paint.setARGB(255,255,0,255);
                break;
            case R.id.BLACK:
                paint.setStrokeWidth(8);
                paint.setColor(Color.BLACK);
                break;
            case R.id.ERASER:
                paint.setColor(Color.WHITE);
                paint.setStrokeWidth(150);
                break;
            case R.id.RESET:
                canvas.drawColor(Color.WHITE);
                break;
            default:
                break;
        }
    }
    public void onDraw(Canvas c) {
        c.drawBitmap(bitmap, 0, 0, null);
    }
}

