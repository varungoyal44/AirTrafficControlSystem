/*
 * AirportList.java
 * 
 * Version:
 * 0.1
 * 
 * Revision:
 * 0.1
 * 
 */


/**
 * This class is to store the distance between two airports.
 * 
 * @author Varun Goyal
 *
 */

public class AirportList
{
	String from;
	String to;
	int dist;

	AirportList(String from, String to, int dist)
	{
		this.from = from;
		this.to = to;
		this.dist = dist;
	}
}
