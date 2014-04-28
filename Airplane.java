/*
 * Airplane.java
 * 
 * Version:
 * 		0.1
 * 
 * Revision:
 * 		0.1
 * 
 */

import java.rmi.RemoteException;

import edu.rit.ds.space.Space;
import edu.rit.ds.space.TupleAndLease;

/**
 * This module handles a flight through its journey from
 * the origin airport to the destination airport.
 * requesting for appropriate corridors from the appropriate airport.
 * and Emergency - landing the plane anywhere if the fuel is over.
 * 
 * USAGE:
 *   	java Airplane <host> <port> <ts> <flight> <origin> < destination>
 *   
 * WHERE:
 * 
 * <host> is the name of the host computer where 
 * 		  the Registry Server is running.
 * 
 * <port> is the port number to which the 
 * 		  Registry Server is listening.
 * 
 * <ts>   is the name of the tuple space bound into the Registry Server.
 * 
 * <flight> is the airplane's flight number, 
 * 		    an arbitrary string like "UA1607".
 * 
 * <origin> is the name of the origin airport.
 * 
 * <destination> is the name of the destination airport. 
 * 
 * 
 * @author 	Varun Goyal
 *
 */


public class Airplane 
{
	//-------------------------------------------------------------------------

	private Space space;

	private String flight;
	private String origin;
	private String destination;

	private String prevAirport;
	private String nextAirport;

	private int distance;
	private float timeRemaining;

	//------------------------------Templates----------------------------------
	// "Airport Present", this.Airport.name...
	Object[] airportPresentTemplate = 
		new Object[] {"Airport Present", null};
	
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
	// "Airport Present", this.Airport.name...
	Object[] airportPresentTuple = 
		new Object[] {"Airport Present", null};
	
	// "Corridor", this.Airport.name, String corridorName, int dist
	Object[] corridorTuple = 
		new Object[] {"Corridor", null, null, null};

	// "Corridor Request", this.Airport.name, String destination, String flightNumber
	Object[] corridorRequestTuple = 
		new Object[] {"Corridor Request", null, null, null};

	// "Airport Response", this.Airport.name, String flightNumber, String via
	Object[] airportResponseTuple = 
		new Object[] {"Airport Response", null, null, null};

	// Lease...
	TupleAndLease corridorTupleLeaseTo = null;
	TupleAndLease corridorTupleLeaseFro = null;

	//-------------------------------------------------------------------------
	/**
	 * This is the constructor to construct the variables and check for 
	 * exceptions in the program.
	 * 
	 * @param	args[]	gets the command line arguments from the user.
	 * 
	 */
	public Airplane (String args[])
	{
		// Initializing...
		AirplaneErrorCheck errorObject = new AirplaneErrorCheck();
		flight = args[3];
		timeRemaining = 20;
		origin = args[4];
		destination = args[5];
		distance = 0;

		//------------------------Check for Errors-----------------------------
		// Any required argument is missing; There are extra arguments.
		errorObject.argumentCheck(args, 6, 
		"java Airplane <host> <port> <ts> <flight> <origin> <destination>");


		// The port argument cannot be parsed as an integer. 
		errorObject.portCheck(args[1]);


		// The origin airport and the destination airport are NOT the same.
		errorObject.checkEquality(origin, destination);


		// To connect to the Registry and get the tuple space object
		// & check their validity...
		space = errorObject.registrySpaceErrorCheck(args[0], Integer.parseInt(args[1]), args[2]);


		// Check if the Origin and Destination airport exist...
		errorObject.checkValidity(space, origin, destination);

		//----------------------To Begin The Journey---------------------------
		try 
		{
			journeyStart(0);
		} 
		catch (RemoteException e) 
		{
			release();
			e.printStackTrace();
		}
		finally
		{
			release();
		}

	}



	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------
	/**
	 * this method gets the nextAirport to which the Airplane can go to
	 * to reach the destination airport.
	 * 
	 * @return	Returns the AirportInterface object.
	 */
	String getNextAirport(String from, String to)
	{
		/*		 
		 Object[] airportPresentTemplate = new Object[] {"Airport Present", null};
		 "Airport Present", this.Airport.name...
		 
		 Object[] corridorRequestTemplate = new Object[] {"Corridor Request", null, null, null};
		"Corridor Request", this.Airport.name, String destination, String flightNumber

		Object[] airportResponseTemplate = new Object[] {"Airport Response", null, null, null};
		"Airport Response", this.Airport.name, String flightNumber, String via

		Object[] corridorTemplate =	new Object[] {"Corridor", null, null, null};
		"Corridor", this.Airport.name, String corridorName, int dist
		 */

		try
		{
			// To check if the approaching airport is Present and not dead...
			airportPresentTemplate = new Object[] {"Airport Present", from};
			airportPresentTuple = null;
			airportPresentTuple = space.read(airportPresentTemplate, 5);
			if(airportPresentTuple == null)
			{
				System.out.println("Emergency: Airport "+from+" failed...");
				emergencyLanding();
			}
			
			// To request for a path from source to destination...
			corridorRequestTemplate = new Object[] {"Corridor Request", from, to, flight};
			corridorRequestTuple = null;
			corridorRequestTuple = space.read(corridorRequestTemplate, 5);
			
			if(corridorRequestTuple == null) // To check if the request was made earlier...
			{
				corridorRequestTuple = new Object[] {"Corridor Request", from, to, flight};
				space.write(corridorRequestTuple);
			}
			
			// To get the response from the airport...
			airportResponseTemplate = new Object[] {"Airport Response", from, flight, null};
			airportResponseTuple = null;
			airportResponseTuple = space.take(airportResponseTemplate, 300);
			
			if(airportResponseTuple == null)
				return null;
			
			String via = (String) airportResponseTuple[3];
			
			// To Take the tuples from the Space...
			corridorTemplate =	new Object[] {"Corridor", from, via, null};
			corridorTuple = null;
			corridorTuple = space.read(corridorTemplate, 5);
			if(corridorTuple != null)
			{
				distance = (Integer) corridorTuple[3];
				corridorTupleLeaseFro = space.takeWithLease(corridorTemplate, 5, ((distance*1000) + 1000));
				if(corridorTupleLeaseFro == null)
				{
					release();
					return null;
				}
			}
			
			corridorTemplate =	new Object[] {"Corridor", via, from, null};
			corridorTupleLeaseTo = space.takeWithLease(corridorTemplate, 5, ((distance*1000) + 1000));
			if(corridorTupleLeaseTo == null)
			{
				release();
				return null;
			}
			return via;
		}
		catch (RemoteException e) 
		{
			release();
			e.printStackTrace();
		} 
		catch (InterruptedException e) 
		{
			release();
			e.printStackTrace();
		}

		return null;
	}

	//-------------------------------------------------------------------------
	/**
	 * This method write the tuple on the tuple space with the messages
	 * to be printed on the monitor's screen.
	 */
	@SuppressWarnings("unused")
	private void writeMonitor(String msg)
	{
		Object[] monitorTemplate = new Object[] {"Monitor", null};	// String msg
		Object[] monitorTuple = new Object[] {"Monitor", msg};

		try 
		{
			space.write(monitorTuple);
		} 
		catch (RemoteException e) 
		{	
			release();
			e.printStackTrace();
		}

	}


	//-------------------------------------------------------------------------
	/**
	 * this method marks the start of the journey.
	 * 
	 * @param	repetition	denotes the number of times the method is called.
	 */
	private void journeyStart(int repetition) throws RemoteException
	{
		if(repetition == 0)
			System.out.println("Flight " + flight +
					" originating from " + origin);

		nextAirport = getNextAirport(origin, destination);

		prevAirport = origin;

		if(nextAirport == null)
		{
			timeRemaining = timeRemaining - 1;
			if (timeRemaining <= 0)
			{
				System.out.println("Emergency: fuel tank empty...");
				emergencyLanding();
			}

			System.out.println("Flight " + flight +" circling");
			journeyStart(1);
		}

		else if(nextAirport.equals(destination))
		{	
			System.out.println("Flight " + flight + " departing for " + 
					nextAirport);
			writeMonitor(prevAirport + " -> " + 
					nextAirport + " Flight " + flight);
			journeyEnd();
		}
		else
		{
			System.out.println("Flight " + flight + " departing for " +
					nextAirport);
			writeMonitor(prevAirport + " -> " + 
					nextAirport + " Flight " + flight);
			journeyContinue(0);
		}
	}

	//-------------------------------------------------------------------------
	/**
	 * marks the continuation of the journey...
	 * 
	 * @param	repetition	# of times the method was called.
	 * @exception	RemoteException		throws the remote exception.
	 */
	private void journeyContinue(int repetition) throws RemoteException
	{
		timeRemaining = timeRemaining - (distance/100);
		if (timeRemaining <= 0)
		{
			System.out.println("Emergency: fuel tank empty...");
			emergencyLanding();
		}

		if (repetition == 0)
		{
			release();
			prevAirport = nextAirport;
			System.out.println("Flight " + flight + " approaching " +
					nextAirport);
		}


		nextAirport = getNextAirport(prevAirport, destination);

		if(nextAirport == null)
		{
			timeRemaining = timeRemaining - 1;
			if (timeRemaining <= 0)
			{
				System.out.println("Emergency: fuel tank empty...");
				emergencyLanding();
			}
			
			System.out.println("Flight " + flight +" circling");
			journeyContinue(1);
		}
		else if(nextAirport.equals(destination))
		{	
			System.out.println("Flight " + flight + " departing for " +
					destination);
			writeMonitor(prevAirport + " -> " + 
					nextAirport + " Flight " + flight);
			journeyEnd();
		}
		else
		{
			System.out.println("Flight " + flight + " departing for " +
					nextAirport);
			writeMonitor(prevAirport + " -> " + 
					nextAirport + " Flight " + flight);
			journeyContinue(0);
		}
	}


	//-------------------------------------------------------------------------
	/**
	 * marks the end of journey...
	 * 
	 * @exception	RemoteException		throws the remote exception.
	 */
	private void journeyEnd() throws RemoteException
	{
		timeRemaining = timeRemaining - (distance/100);
		if (timeRemaining <= 0)
		{
			System.out.println("Emergency: fuel tank empty...");
			emergencyLanding();
		}

		release();

		System.out.println("Flight " + flight + " landing at " + destination);
		System.exit(0);
	}


	//-------------------------------------------------------------------------
	/**
	 * method to perform necessary actions incase of empty fuel tank.
	 * 
	 * @exception	RemoteException		throws the remote exception.
	 */
	private void emergencyLanding() throws RemoteException
	{
		release();
		System.out.println("Flight " + flight + "emergency landing");
		System.exit(0);
	}


	//-------------------------------------------------------------------------
	/**
	 * To release the tuple bound to the plane...
	 * 
	 */
	private void release()
	{
		if (corridorTupleLeaseTo != null && corridorTupleLeaseTo.lease != null)
			corridorTupleLeaseTo.lease.expire();

		if (corridorTupleLeaseFro != null  && corridorTupleLeaseFro.lease != null)
			corridorTupleLeaseFro.lease.expire();
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
		new Airplane(args);
	}
}
//--------------------------------XXX------------------------------------------