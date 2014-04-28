/*
 * Graph.java
 * 
 * Version:
 * 		0.1
 * 
 * Revision:
 * 		0.1
 * 
 */

import java.util.ArrayList;

/**
 * This method creates the graph to be used in the Dijkstra's algorithm.
 * 
 * @author Varun Goyal
 *
 */

public class Graph 
{
	
	//-------------------------------------------------------------------------
	private Node thisNode;
	private ArrayList <Node> allNodes = new ArrayList<Node>();
	private ArrayList <Integer> visitedNodes = new ArrayList<Integer>(); 

	//-------------------------------------------------------------------------
	/**
	 * To initialize the node to be added to the graph...
	 * 
	 * @param	thisNode	gives the node to be populated to allNodes.
	 */
	public Graph(Node thisNode)
	{
		this.thisNode = thisNode;
		this.discoverGraph();
	}

	//-------------------------------------------------------------------------
	/**
	 * getter to get the last added node...
	 * 
	 * @return		returns the last added node...
	 */
	public Node getthisNode()
	{
		return this.thisNode;
	}

	//-------------------------------------------------------------------------
	/**
	 * getter to return all the nodes (the whole graph)
	 * 
	 * @return		returns all the nodes.
	 * 
	 */
	public ArrayList<Node> getAllNodes()
	{
		return this.allNodes;
	}


	//-------------------------------------------------------------------------
	/**
	 * to search for the unvisited nodes in the graph...
	 * 
	 */
	private void discoverGraph()
	{
		allNodes.add(this.thisNode);
		visit(thisNode);
		for (Edge e : this.thisNode.getOutGoingEdges()){
			if (!isVisited(e.getNode()))
			{
				bfs(e.getNode());
			}
		}
	}

	//-------------------------------------------------------------------------
	/**
	 * to breadth first search...
	 */
	private void bfs(Node n)
	{
		visit(n);
		this.allNodes.add(n);
		for (Edge e: n.getOutGoingEdges())
		{
			if (!isVisited(e.getNode()))
			{	
				bfs(e.getNode());
			}
		}
	}

	//-------------------------------------------------------------------------
	/**
	 * To see if the said node is visited or not.
	 * 
	 * @param		n		the node to be checked if it is visited.
	 * 
	 * @return		returns true if visited.
	 *  
	 */
	private boolean isVisited(Node n)
	{
		return visitedNodes.contains(n.getID());
	}

	//-------------------------------------------------------------------------
	/**
	 * To "visit" the node.
	 * 
	 * @param		n		node to be visited.
	 *	
	 */
	private void visit(Node n)
	{
		this.visitedNodes.add(n.getID());
	}

	//-------------------------------------------------------------------------
	/**
	 * to return the calculated shortest distance.
	 * 
	 * @return		returns the calculated distance.
	 * 
	 */
	public int[] returnDistance(int size)
	{
		int i=0;
		int[] ret = new int [size];
		
		for (Node n: this.allNodes)
		{
			ret[i] = n.getDistance();
			i++;
		}
		return ret;
	}
}
//--------------------------------XXX------------------------------------------