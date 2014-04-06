package com.enable.navigator;

import java.util.Set;
import java.util.TreeSet;

/*
 * Node class for containing the Graph
 */

public class Node {
	public int x = 0;
	public int y = 0;
	public int index = 0;
	public String label = "empty";
	public double scale = 0.25;
	public Node left = null;
	public Node right = null;
	public Node up = null;
	public Node down = null;
	public Node upRight = null;
	public Node upLeft = null;
	public Node downLeft = null;
	public Node downRight = null;
	public Boolean active = false;
	public Boolean exists = true;
	
	public double getDistance(Node node){
		double dist = Math.sqrt((node.y - this.y)*(node.y - this.y) + (node.x - this.x)*(node.x - this.x));
		return dist;
	}
	
	//Upto a List of Nodes doing BFS till 'level' neighbours. level>=1
	public Set<Integer> getNodes(int level)	{
		Set<Integer> li = new TreeSet<Integer>();
		Set<Integer> temp;
		li.add(this.index);
		if (level>=1)	{
			if (this.left != null)	{
				temp = this.left.getNodes(level-1);
				li.addAll(temp);
			}
			if (this.right != null)	{
				temp = right.getNodes(level-1);
				li.addAll(temp);
			}
			if (this.up != null)	{
				temp = up.getNodes(level-1);
				li.addAll(temp);
			}
			if (this.down != null)	{
				temp = down.getNodes(level-1);
				li.addAll(temp);
			}
			if (this.upRight != null)	{
				temp = upRight.getNodes(level-1);
				li.addAll(temp);
			}
			if (this.downRight != null)	{
				temp = downRight.getNodes(level-1);
				li.addAll(temp);
			}
			if (this.upLeft != null)	{
				temp = upLeft.getNodes(level-1);
				li.addAll(temp);
			}
			if (this.downLeft != null)	{
				temp = downLeft.getNodes(level-1);
				li.addAll(temp);
			}
			if (this.right != null)	{
				temp = right.getNodes(level-1);
				li.addAll(temp);
			}
			if (this.right != null)	{
				temp = right.getNodes(level-1);
				li.addAll(temp);
			}
		}
		return li;
	}
}
