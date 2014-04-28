/*
 * Airport.java
 * 
 * Version:
 * 		0.1
 * 
 * Revision:
 * 		0.1
 * 
 */

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;

import edu.rit.ds.Lease;
import edu.rit.ds.space.Space;


//-----------------------------------------------------------------------------
/**
 * This module is to calculate the shortest path between the airport 
 * passed as a command line 
 * and 
 * the rest of the airport mentioned in the configFile. 
 * 
 * Then this module waits for any Airplane's request for shortest 
 * path to any other airport.
 * 
 * USAGE: 
 *     java Airport <host> <port> <ts> <name> <configfile>
 *     
 * WHERE:    
 * <host> is the name of the host computer where the 
 * 		  Registry Server is running.
 * 
 * <port> is the port number to which the 
 *        Registry Server is listening.
 *        
 * <ts>   is the name of the tuple space bound into the Registry Server.
 *        
 * <name> is the airport's name as it appears in the configuration file 
 *        and is also the name of the airport bound into the Registry Server.
 *        
 * <configfile> is the name of the plain text configuration file. 
 * 
 * 
 * @author   Varun Goyal
 *
 */


public class Airport 
{

	//-------------------------------------------------------------------------
	private String name;
	private Space space;

	private int m;
	private int noOfCorridor;
	AirportCorridor corridor[];


	//------------------------------Templates----------------------------------
	// "Airport Present", this.Airport.name...
	Object[] airportPresentTemplate = 
		new Object[] {"Airport Present", null};

	// "Valid Airport", int noOfAirports, String[] Airports
	Object[] validAirportTemplate = 
		new Object[] {"Valid Airport", null, null};

	// "Corridor", this.Airport.name, String corridorName, int dist
	Object[] corridorTemplate = 
		new Object[] {"Corridor", null, null, null};

	// "Corridor Request", this.Airport.name, String destination, String flightNumber
	Object[] corridorRequestTemplate = 
		new Object[] {"Corridor Request", null, null, null};

	// "Airport Response", this.Airport.name, String flightNumber, String via
	Object[] airportResponseTemplate = 
		new Object[] {"Airport Response", null, null, null};

	//--------------------------------Tuples-----------------------------------
	// "Valid Airport", int noOfAirports, String[] Airports
	Object[] validAirportTuple = 
		new Object[] {"Valid Airport", null, null};

	// "Corridor", this.Airport.name, String corridorName, int dist
	Object[] corridorTuple = 
		new Object[] {"Corridor", null, null, null};

	// "Corridor Request", this.Airport.name, String destination, String flightNumber
	Object[] corridorRequestTuple = 
		new Object[] {"Corridor Request", null, null, null};

	// "Airport Response", this.Airport.name, String flightNumber, String via
	Object[] airportResponseTuple = 
		new Object[] {"Airport Response", null, null, null};

	//--------------------------------Lease------------------------------------
	ArrayList<Lease> leaseList = new ArrayList<Lease>(); 

	//-------------------------------------------------------------------------
	/**
	 * This is the constructor to construct the variables and check for 
	 * exceptions in the program.
	 * 
	 * @param	args[]	gets the command line arguments from the user.
	 * 
	 * @throws	RemoteException	to throw RemoteExceptions
	 * 
	 */
	public Airport(java.lang.String[] args) throws RemoteException
	{
		//------------------------Initialize variables-------------------------
		name = args[3];
		AirportErrorCheck errorObject = new AirportErrorCheck();
		AirportCalculation calculationObject = 
			new AirportCalculation(name, new File(args[4]));

		//------------------------Check for Errors-----------------------------
		// Any required argument is missing / There are extra arguments...
		errorObject.argumentCheck(args, 5, 
		"java Airport <host> <port> <ts> <name> <configfile>");


		// The port argument cannot be parsed as an integer... 
		errorObject.portCheck(args[1]);


		// The configuration file cannot be read...
		// & The configuration file's contents are invalid...
		calculationObject.fileErrorCheck(args[4]);


		// The airport's name on the command line 
		// does not appear in the configuration file. 
		calculationObject.validAirportName();


		// To connect to the Registry and get the tuple space object
		// & check their validity...
		space = errorObject.registrySpaceErrorCheck(args[0], Integer.parseInt(args[1]), args[2]);


		// Check if the airport is already alive in the tuple space...
		try {

			/*
			 * Object[] airportPresentTemplate = new Object[] {"Airport Present", null};
			 * "Airport Present", this.Airport.name...
			 */

			Object[] airportPresentTuple = null;
			airportPresentTemplate[0] = "Airport Present";
			airportPresentTemplate[1] = name;
			airportPresentTuple = space.read(airportPresentTemplate, 5);

			if(airportPresentTuple != null)
			{
				System.out.println("Error: Airport already present...");
				System.out.println("exiting....");
				System.exit(0);
			}
			else
			{
				airportPresentTuple = new Object[] {"Airport Present", name};
				Lease temp = space.write(airportPresentTuple, Integer.MAX_VALUE);
				leaseList.add(temp);
				
			}

		} 
		catch (InterruptedException e) 
		{
			releaseLease();
			e.printStackTrace();
		}


		// To calculate and print the shortest paths...
		calculationObject.calculatePrint();


		// To initialize the corridor...
		calculationObject.intializeCorridorDist();


		// To get calculated values...
		m = calculationObject.getM();
		noOfCorridor = calculationObject.getNoOfCorridor();
		corridor = calculationObject.getCorridor();


		// To set the value for valid Airports in the server...
		/*
		 * Object[] validAirportTemplate = new Object[] {"Valid Airport", null, null};
		 * "Valid Airport", int noOfAirports, String[] Airports
		 */
		validAirportTuple[0] = "Valid Airport";
		validAirportTuple[1] = m;
		validAirportTuple[2] = calculationObject.getValidAirports();
		Lease temp = space.write(validAirportTuple, Integer.MAX_VALUE);
		leaseList.add(temp);


		// To write tuples...
		writeCorridorTuples();

		// Await Airplane request...
		waitForRequest();

	}

	
	
	//-------------------------------------------------------------------------
	/**
	 * Gives the list of corridors which a plane can take, in fastest first 
	 * sorted order.
	 * 
	 * @param		to					tells what is the destination.
	 * 
	 * 	
	 * @return		returns the BEST corridor/airport route to be takes.
	 * 
	 */
	private ArrayList <String> outgoingCorridor(String to) 
	{
		for(int i=0; i<m-1; i++)
		{
			if(corridor[i].to.equals(to))
			{
				return corridor[i].via;
			}
		}
		return null;
	}


	//-------------------------------------------------------------------------
	/**
	 * Method to write the tuples on the tuple space.
	 * tuples of the list of airports via which one can go.
	 * 
	 */
	private void writeCorridorTuples()
	{
		/*
		 * Object[] corridorTemplate =	new Object[] {"Corridor", null, null, null};
		 * "Corridor", this.Airport.name, String corridorName, int dist
		 */
		try 
		{
			for(int i=0; i<m-1; i++)
			{
				for(int j=0; j<noOfCorridor; j++)
				{
					corridorTuple[0] = "Corridor";
					corridorTuple[1] = name;
					corridorTuple[2] = corridor[i].via.get(j);
					corridorTuple[3] = corridor[i].dist.get(j);
					
					Lease temp = space.write(corridorTuple, Integer.MAX_VALUE);
					leaseList.add(temp);
				}
			}
		}
		catch (RemoteException e) 
		{
			releaseLease();
			e.printStackTrace();
		}
	}


	//-------------------------------------------------------------------------
	/**
	 * This method waits for the airport to request for a path via this
	 * airport to another one.
	 * 
	 */
	private void waitForRequest()
	{
		/*
		 * Object[] corridorRequestTemplate = new Object[] {"Corridor Request", null, null, null};
		 * "Corridor Request", this.Airport.name, String destination, String flightNumber
		 * 
		 * Object[] airportResponseTemplate = new Object[] {"Airport Response", null, null, null};
		 * "Airport Response", this.Airport.name, String flightNumber, String via
		 * 
		 * Object[] corridorTemplate =	new Object[] {"Corridor", null, null, null};
		 * "Corridor", this.Airport.name, String corridorName, int dist
		 */
		while(true)
		{
			try
			{
				corridorRequestTemplate = new Object[] {"Corridor Request", name, null, null};
				corridorRequestTuple = space.take(corridorRequestTemplate);

				String dest = (String)corridorRequestTuple[2];
				String flightNumber = (String) corridorRequestTuple[3];
				ArrayList <String> via = outgoingCorridor(dest);
				
				for(int i=0; i<noOfCorridor; i++)
				{
					corridorTemplate =	new Object[] {"Corridor", name, via.get(i), null};
					corridorTuple = space.read(corridorTemplate, 5);
					
					Object[] corridorTemplate1 =	new Object[] {"Corridor", via.get(i), name, null};
					Object[] corridorTuple1 = space.read(corridorTemplate1, 5);
					
					if(corridorTuple != null && corridorTuple1 != null)
					{
						airportResponseTuple = new Object[] {"Airport Response", name, flightNumber, via.get(i)};
						space.write(airportResponseTuple, 1200);
						break;
					}
				}
			}
			catch (RemoteException e) 
			{
				releaseLease();
				e.printStackTrace();
			} 
			catch (InterruptedException e) 
			{
				releaseLease();
				e.printStackTrace();
			}
		}
	}

	//-------------------------------------------------------------------------
	/**
	 * To release the lease on all the write operations performed.
	 */
	void releaseLease()
	{
		Lease temp;
		Iterator<Lease> itr = leaseList.iterator();
		while(itr.hasNext())
		{
			temp = (Lease) itr.next(); 
			if(temp != null)
				temp.expire();
		}
	}
	
	
	//-------------------------------------------------------------------------
	/**
	 * the main method.
	 * @param 	args	command line argument to get the arguments from the
	 * 					command line like server to be 
	 * 					connected to and port etc.
	 */
	public static void main(String args[])
	{
		Airport airportObject = null;
		try 
		{
			airportObject = new Airport(args);
		}
		catch (RemoteException e) 
		{
			airportObject.releaseLease();
			e.printStackTrace();
		}
		finally
		{
			if (airportObject != null)
			airportObject.releaseLease();
		}
	}
}
//--------------------------------XXX------------------------------------------