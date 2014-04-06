package com.enable.navigator;
/*
 * Node class for containing the Graph
 */

public class Node {
	public int x = 0;
	public int y = 0;
	public Node left = null;
	public Node right = null;
	public Node up = null;
	public Node down = null;
	public Node upRight = null;
	public Node upLeft = null;
	public Node downLeft = null;
	public Node downRight = null;
	
	public double getDistance(Node node){
		double dist = Math.sqrt((node.y - this.y)*(node.y - this.y) + (node.x - this.x)*(node.x - this.x));
		return dist;
	}
}
