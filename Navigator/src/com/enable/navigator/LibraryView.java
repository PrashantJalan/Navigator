package com.enable.navigator;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import com.enable.navigator.Polygon.Builder;

public class LibraryView extends View{
	
	Paint paint_black = new Paint(Paint.ANTI_ALIAS_FLAG);
	Paint paint_red = new Paint(Paint.ANTI_ALIAS_FLAG);
	Paint paint_blue = new Paint(Paint.ANTI_ALIAS_FLAG);
	float direction;
	
	float x_max = 14;
    float y_max = 8;
	float distance = (float) 0.25;
    int numColumns = (int) ((x_max / distance) + 1);
    int numRows  = (int) ((y_max / distance) + 1);
    Node[][] grid = new Node[numRows][numColumns];	
	Context context;
	
	public LibraryView(Context context2) {
		super(context2);
		// TODO Auto-generated constructor stub
		
		//Making the graph
		numColumns = (int) ((x_max / distance) + 1);
	    numRows  = (int) ((y_max / distance) + 1);
	    grid = new Node[numColumns][numRows];
	    for (int i = 0; i<numColumns; i++){
            for (int j = 0; j<numRows; j++){
            	grid[i][j] = new Node();
            	grid[i][j].x = i * distance;
            	grid[i][j].y = j * distance;
            }
        }
	    
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
	
	public class Pnt {
		float x;
		float y;
	}
	
	public Pnt makePnt (float x, float y){
		Pnt p = new Pnt();
		p.x = x;
		p.y = y;
		return p;
	}
	
	public void onDraw(Canvas canvas) {
		
		float height = this.getHeight();
	    float width = this.getWidth();
		float radius = 2;
	    
		List<Pnt> points = new ArrayList<Pnt>();
		points.add(makePnt((float)14,(float)8));
		points.add(makePnt((float)11,(float)8));
		points.add(makePnt((float)11,(float)5));
		points.add(makePnt((float)9.5,(float)5));
		points.add(makePnt((float)9.5,(float)8));
		points.add(makePnt((float)0,(float)8));
		points.add(makePnt((float)0,(float)0));
		points.add(makePnt((float)4,(float)0));
		points.add(makePnt((float)4,(float)6.5));
		points.add(makePnt((float)8,(float)6.5));
		points.add(makePnt((float)8,(float)2));
		points.add(makePnt((float)7,(float)2));
		points.add(makePnt((float)7,(float)0));
		points.add(makePnt((float)14,(float)0));
		
		float compress_x = width / x_max;
		float compress_y = height / y_max;
		float compress = Math.min(compress_x, compress_y);
		
		Builder builder = Polygon.Builder();
		       
		// transform coordinates and scale
		for (int i=0; i<points.size(); i++){
			builder.addVertex(new Point(points.get(i).x, points.get(i).y));
			points.get(i).x = (points.get(i).x) * compress;
			points.get(i).y = (points.get(i).y) * compress + ((height - y_max*compress)/2);
		}
		Polygon polygon = builder.build();
		for (int i=0; i<points.size(); i++){
			float x_cur = points.get(i).x;
			float x_next = points.get((i+1)%points.size()).x;
			float y_cur = points.get(i).y;
			float y_next = points.get((i+1)%points.size()).y;
			canvas.drawLine(x_cur, y_cur, x_next, y_next, paint_black);
			
		}
		
		for (int i = 0; i<numColumns; i++){
            for (int j = 0; j<numRows; j++){
            	float center_x = (float) (grid[i][j].x * compress);
            	float center_y = (float) (grid[i][j].y * compress + ((height - y_max*compress)/2));
            	Point pt = new Point((float) grid[i][j].x, (float) grid[i][j].y);
            	if (!polygon.contains(pt))	{ 
            		grid[i][j].exists = false;
            	}
            	else	{
                	if (grid[i][j].active)
                    	canvas.drawCircle(center_x, center_y, radius, paint_black);
                    else
                    	canvas.drawCircle(center_x, center_y, radius, paint_red);
            	}
            }
		}
		
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

	public void nodeChanged() {
		// TODO Auto-generated method stub
		invalidate();
	}

	public Node[][] getGrid()	{
		return grid;
	}
	
	public void setActiveTrue(int x2, int y2)	{
		grid[x2][y2].active = true;
	}
	
	public void setActiveFalse(int x2, int y2)	{
		grid[x2][y2].active = false;
	}
}

