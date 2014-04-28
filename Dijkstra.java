/*
 * Dijkstra.java
 * 
 * Version:
 * 		0.1
 * 
 * Revision:
 * 		0.1
 * 
 */

import java.util.Hashtable;

/**
 * This class is to calculate the shortest path using Dijkstra's Algorithm...
 * 
 * @author Varun Goyal
 *
 */

public class Dijkstra 
{	
	private Graph graph;

	/**
	 * Priority queue stores all of the nodes, reachable from the start node
	 * The queue is sorted by the node.distance
	 * 
	 */
	private NodePriorityQueue priorityQ = new NodePriorityQueue();

	@SuppressWarnings("unused")
	private Hashtable <Node,Integer> distance = new Hashtable<Node, Integer>();

	
	//-------------------------------------------------------------------------
	/**
	 * The method to calculate the shortest path using Dijkstra's Algorithm.
	 * 
	 * This method:
	 * 		- needs to get the list of all nodes in the graph
	 * 		- needs to initialize distance vector to infinity
	 * 		- needs Edge Cost function
	 * 
	 * @param g		to get the graph which is to be used to get 
	 * 				the shortest path from.
	 * 
	 */
	public Dijkstra (Graph g)
	{
		this.graph  = g;
		this.graph.getthisNode().setDistance(0);
		this.priorityQ.add(this.graph.getAllNodes());
	}

	//-------------------------------------------------------------------------
	/**
	 * This is the actual algorithm
	 */
	public void go()
	{
		while (this.priorityQ.hasMore())
		{
			Node n = this.priorityQ.remove();
			for (Edge e: n.getOutGoingEdges())
			{
				Node adjNode = e.getNode();
				Integer newPossiblePathCost = e.getCost()+n.getDistance();
				if (newPossiblePathCost<adjNode.getDistance())
				{
					adjNode.setDistance(newPossiblePathCost);
					this.priorityQ.updateNodeDistance(adjNode);
				}
			}
		}	
	}
}
//--------------------------------XXX------------------------------------------