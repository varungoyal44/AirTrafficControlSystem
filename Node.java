/*
 * Node.java
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
 * this class is to create a new weighted edge between two nodes.
 * 
 * @author Varun Goyal
 *
 */

final class Edge 
{
	private Node node;
	private Integer cost;

	/**
	 * constructor to initialize the class variables.
	 * 
	 * @param 	nodeTo		denotes which node this edge is connected to.
	 * @param 	cost		denotes the cost of the edge.
	 * 
	 */
	public Edge(Node nodeTo, Integer cost) 
	{
		this.node = nodeTo;
		this.cost = cost;
	}

	/**
	 * getter to get the node connected to this edge...
	 * 
	 * @return	returns this node.
	 * 
	 */
	public Node getNode() 
	{
		return node;
	}

	/**
	 * getter to get the cost of this edge...
	 * 
	 * @return	returns the cost of this edge.
	 * 	
	 */
	public Integer getCost()
	{
		return cost;
	}
}


/**
 * this class is to create the nodes...
 * 
 * @author Varun Goyal
 *
 */
public class Node implements Comparable<Node> 
{
	private static int nodeCount=0;

	private ArrayList<Edge> outGoingEdges = new ArrayList<Edge>();
	private String val;
	private Integer ID;
	
	@SuppressWarnings("unused")
	private boolean visited;
	
	private Integer distance = Constant.INFINITY;

	
	/**
	 * constructor to initialize this node...
	 * 
	 * @param 	value	value by which the node has to initialized.
	 *  
	 */
	public Node(String value)
	{
		this.init(value);
	}

	/**
	 * constructor to initialize this node by null value...
	 * 
	 */
	public Node()
	{
		this.init("");
	}

	/**
	 * to actually perform the initialization...
	 * 
	 * @param 	nodeVal		value to be initialized with.
	 * 
	 */
	private void init(String nodeVal)
	{
		this.val = nodeVal;
		this.ID = Node.nodeCount++;
		this.visited = false;
	}

	/**
	 * to set if/not the node is visited while traversing...
	 * 
	 * @param 	visited		gives whether the node is visited or not. 
	 */
	public void setVisited(boolean visited)
	{
		this.visited = visited;
	}

	/**
	 * to add a new outgoing edge to the node...
	 * 
	 * @param 	node	node to which the edges goes to.
	 * @param 	cost	cost of the edge.
	 */
	public void AddOutgoingEdge(Node node,Integer cost) 
	{
		this.outGoingEdges.add(new Edge(node,cost));
	}

	/**
	 * getter to the the outgoing edges from this.node...
	 * 
	 * @return	returns the outgoing edges from this.node
	 * 
	 */
	public ArrayList<Edge> getOutGoingEdges()
	{
		return outGoingEdges;
	}

	/**
	 * getter to get the value of the node...
	 * 
	 * @return	returns the value of the node. 	
	 */
	public String getVal()
	{
		return val;
	}

	/**
	 * to set a new value to the node...
	 * 
	 * @param 	val		value to be set.
	 * 
	 */
	public void setVal(String val)
	{
		this.val = val;
	}

	/**
	 * getter to get the ID of the node...
	 * 
	 * @return		returns the ID of the node.
	 */
	public Integer getID() 
	{
		return ID;
	}

	/**
	 * to compare the distances between the nodes and this node...
	 * 
	 * @param	arg0	specifies which node is to be compared with.
	 * 
	 * @return		returns the compare value.
	 * 
	 */
	public int compareTo(Node arg0) 
	{
		return this.distance.compareTo(arg0.getDistance());
	}

	/**
	 * getter to get the distance...
	 * 
	 * @return	returns the value of distance variable.
	 */
	public Integer getDistance() 
	{
		return distance;
	}

	/**
	 * setter to set the distance...
	 * 
	 * @param 	distance	the value by which this distance is to be set.
	 */
	public void setDistance(Integer distance)
	{
		this.distance = distance;
	}
}
//--------------------------------XXX------------------------------------------