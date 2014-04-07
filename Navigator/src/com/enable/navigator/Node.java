package com.enable.navigator;

import java.util.ArrayList;
import java.util.List;

/*
 * Node class for containing the Graph
 */

public class Node {
	public double x = 0;
	public double y = 0;
	public String label = "empty";
	Boolean active = false;
	Boolean exists = true;
	List<Integer> RSS = new ArrayList<Integer>();
	
	public double getDistance(Node node){
		double dist = Math.sqrt((node.y - this.y)*(node.y - this.y) + (node.x - this.x)*(node.x - this.x));
		return dist;
	}
	
	public double getCoordinateDistance(double x2, double y2)	{
		double dist = Math.sqrt((this.x - x2)*(this.x - x2) + (this.y - y2)*(this.y - y2));
		return dist;
	}
	
	/*
				int startX = (int) Math.max(root.x*scale - level, 0);
				int startY = (int) Math.max(root.y*scale - level, 0);
				int endX = (int) Math.min(root.x*scale + level, 14);
				int endY = (int) Math.min(root.y*scale + level, 8);
				for (int i=startX; i<= endX; i++)	{
					for (int j=startY; j<= endY; j++)	{
						nodeDistance = nodeGrid[i][j].getCoordinateDistance(newX, newY);
						if (nodeDistance < min){
							min = nodeDistance;
							newNode.x = i;
							newNode.y = j;
						}
					}
				}
	*/
}
