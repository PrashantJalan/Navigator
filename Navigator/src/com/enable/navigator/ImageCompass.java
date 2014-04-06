package com.enable.navigator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class ImageCompass extends View{
	Paint paint_black = new Paint(Paint.ANTI_ALIAS_FLAG);
	Paint paint_red = new Paint(Paint.ANTI_ALIAS_FLAG);
	Paint paint_blue = new Paint(Paint.ANTI_ALIAS_FLAG);
	float direction;
	Context context;
	
	public ImageCompass(Context context2) {
		super(context2);
		// TODO Auto-generated constructor stub
		
		context = context2;
		paint_black.setColor(Color.BLACK);
		paint_black.setStyle(Paint.Style.STROKE);
		paint_black.setStrokeWidth(5);
		paint_red.setColor(Color.RED);
		paint_red.setStyle(Paint.Style.STROKE);
		paint_red.setStrokeWidth(5);
		paint_blue.setColor(Color.BLUE);
		paint_blue.setStyle(Paint.Style.STROKE);
	}
	
	public void onDraw(Canvas canvas) {
		float height = this.getHeight();
	    float width = this.getWidth();
	    float radius = Math.min(height/2, width/2) - 100;
	    float end_x = (float) (width/2+radius*Math.sin(direction));
	    float end_y =  (float) (height/2-radius*Math.cos(direction));
		
	    canvas.drawCircle(width/2, height/2, radius, paint_black);
	    canvas.drawLine(width/2, height/2, width/2, height/2-radius, paint_black);
	    canvas.drawLine(width/2, height/2, end_x, end_y, paint_red);
	    canvas.drawText(Double.toString(Math.round(Math.toDegrees(direction)))+" degrees to the North", width/2-100, height/5, paint_blue);
	    super.onDraw(canvas);
	}

	public void setValue(float[] orientation) {
		// TODO Auto-generated method stub
		if (orientation[0]<0)
			direction = -orientation[0];
		else
			direction = (float) (-orientation[0]+ 2*3.143);
		invalidate();
	}

}
