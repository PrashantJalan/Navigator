package com.enable.navigator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class GraphView extends View{
    Paint paint_black = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint paint_red = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint paint_blue = new Paint(Paint.ANTI_ALIAS_FLAG);
   
    float x_max = 14;
    float y_max = 8;
    float distance = (float) 0.25;
    int numColumns = (int) ((x_max / distance) + 1);
    int numRows  = (int) ((y_max / distance) + 1);
    Node[][] grid = new Node[numRows][numColumns];  
    Context context;
    
    public GraphView(Context context2, Node[][] nodeGrid) {
        super(context2);
        // TODO Auto-generated constructor stub
        
        grid = nodeGrid;
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
    public class Point {
        float x;
        float y;
    }
    public Point makePoint (float x, float y){
        Point p = new Point();
        p.x = x;
        p.y = y;
        return p;
    }
    public void onDraw(Canvas canvas) {
        
        float height = this.getHeight();
        float width = this.getWidth();
        float radius = 2;
     
        float compress_x = width / x_max;
        float compress_y = height / y_max;
        float compress = Math.min(compress_x, compress_y);
        
        for (int i = 0; i<numColumns; i++){
            for (int j = 0; j<numRows; j++){
                float center_x = (float) (grid[i][j].x * compress);
                float center_y = (float) (grid[i][j].y * compress + ((height - y_max*compress)/2));
                if (grid[i][j].active)
                	canvas.drawCircle(center_x, center_y, radius, paint_black);
                else
                	canvas.drawCircle(center_x, center_y, radius, paint_red);
            }
        }
        super.onDraw(canvas);
    }

    public void nodeChanged()	{
    	invalidate();
    }
}
