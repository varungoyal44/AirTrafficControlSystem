/*
 * NodePriorityQueue.java
 * 
 * Version:
 * 		0.1
 * 
 * Revision:
 * 		0.1
 * 
 */

import java.util.Collection;
import java.util.PriorityQueue;

/**
 * This class sets and gets the priority queue of the nodes...
 * 
 * @author Varun Goyal
 *
 */
public class NodePriorityQueue  
{
	private PriorityQueue<Node> priorityQueue = new PriorityQueue<Node>();

	/**
	 * to add a node to the priority queue...
	 * 
	 * @param 	n	node to be added.
	 * 
	 */
	public void add(Node n)
	{
		priorityQueue.add(n);
	}

	
	/**
	 * to add a collection of nodes to the priority queue...
	 * 
	 * @param 	nodeCollection	gives the collection of the nodes.
	 * 
	 */
	public void add(Collection<Node> nodeCollection)
	{
		this.priorityQueue.addAll(nodeCollection);
	}


	/**
	 * checks if the priority queue is empty or not...
	 * 
	 * @return		returns false if the priority queue is empty.
	 * 
	 */
	public Boolean hasMore()
	{
		return !this.priorityQueue.isEmpty();
	}

	
	/**
	 * to remove a node from the priority queue.
	 * 
	 * @return		returns the removed node.
	 * 
	 */
	public Node remove()
	{
		return this.priorityQueue.remove();
	}

	/**	
	 * Removes desired graph node, then inserts into appropriate slot...
	 * 
	 * @param 	n	denotes the node to be shifted.
	 */
	public void updateNodeDistance(Node n)
	{
		this.priorityQueue.remove(n);
		this.priorityQueue.add(n);
	}
}
