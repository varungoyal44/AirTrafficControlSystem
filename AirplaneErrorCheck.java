/*
 * AirplaneErrorCheck.java
 * 
 * Version:
 * 0.1
 * 
 * Revision:
 * 0.1
 */

import java.rmi.RemoteException;

import edu.rit.ds.registry.RegistryProxy;
import edu.rit.ds.space.Space;


/**
 * This class is meant to check for various errors before the 
 * actual execution of the airplane...
 * 
 * @author Varun Goyal
 *
 */


public class AirplaneErrorCheck 
{
	//-------------------------------------------------------------------------
	/**
	 * To check if the port given is a valid integer value.
	 * 
	 *  @param	port	whose value is to be analysed.
	 */
	public void portCheck(String port)
	{
		try{
			Integer.parseInt(port);
		}catch(NumberFormatException e)
		{
			System.out.println("Error 01: Invalid port number...");
			System.out.println("exiting...");
			System.exit(0);
		}
	}


	
	//-------------------------------------------------------------------------
	/**
	 * this method checks for irregularities in the command line arguments.
	 * 
	 * @param	args[]	gets the command line arguments from the user.
	 * 
	 */
	public void argumentCheck(String args[], int requiredNoOfArgs, String requiredSyntax)
	{
		int len = args.length;

		if(len < requiredNoOfArgs)
		{
			System.out.println("Error: Argument Missing...");
			System.out.println("Default: " +requiredSyntax);
			System.out.println("exiting...");
			System.exit(0);
		}

		if(len > requiredNoOfArgs)
		{
			System.out.println("Error: Extra Argument provided...");
			System.out.println("Default: " +requiredSyntax);
			System.out.println("exiting...");
			System.exit(0);
		}
	}


	//-------------------------------------------------------------------------
	/**
	 * to check if the source and the destination has been set the same.
	 * 
	 */
	public void checkEquality (String origin, String destination)
	{
		if(origin.equals(destination))
		{
			System.out.println("Error: Origin airport is " +
					"same as Destination - " + destination);
			System.out.println("exiting....");
			System.exit(0);
		}
	}


	//-------------------------------------------------------------------------
	/**
	 * This method is meant to connect the airport to the registry server
	 * and thus link to the tuple space. and thus check for any connection
	 * errors.
	 * 
	 * @param host		specifies the host to be connected to.
	 * @param port		specifies the port number
	 * @param ts		specifies the Tuple Space name.
	 * @return			returns the bounded tuple space Space.
	 */
	public Space registrySpaceErrorCheck(String host, int port, String ts)
	{
		try
		{
			RegistryProxy registry = new RegistryProxy (host, port);
			return( (Space) registry.lookup(ts) );
		}
		catch(RemoteException e)
		{
			System.out.println("Error: Could not connect " +
			"to the Registry server.");
			System.out.println("exiting....");
			System.exit(0);
		}
		catch (edu.rit.ds.registry.NotBoundException e) 
		{
			System.out.println("Error: space not bound to the registry...");
			System.out.println("exiting....");
			System.exit(0);
		}

		return null;
	}

	//-------------------------------------------------------------------------
	/**
	 * To check if the origin and the destination airports are set correctly.
	 * 
	 */
	public void checkValidity(Space space, String origin, String destination)
	{
		try
		{		
			// The origin airport does not exist.
			Object[] validAirportTemplate = new Object[] {"Valid Airport", null, null};	// int noOfAirports, String[] Airports
			Object[] validAirportTuple;

			validAirportTuple = space.read(validAirportTemplate, 5);
			if(validAirportTuple == null)
			{
				System.out.println("Error: No airports in operation...");
				System.out.println("exiting....");
				System.exit(0);
			}


			int noOfAirports = (Integer) validAirportTuple[1];
			String[] airports = (String[]) validAirportTuple[2]; 

			boolean validOrigin = false;
			boolean validDest = false;

			for(int i=0; i<noOfAirports; i++)
			{
				if(airports[i].equals(origin))
					validOrigin = true;

				if(airports[i].equals(destination))
					validDest = true;
			}

			// The origin airport does not exist. 
			if(validOrigin == false)
			{
				System.out.println("Error: Invalid Origin " +
						"airport: " + origin);
				System.out.println("exiting....");
				System.exit(0);
			}

			// The destination airport does not exist. 
			if(validDest == false)
			{
				System.out.println("Error: Invalid Destination " +
						"airport: " + destination);
				System.out.println("exiting....");
				System.exit(0);
			}

		}
		catch(RemoteException e)
		{
			System.out.println("Error: Could not connect " +
			"to the Registry server.");
			System.out.println("exiting....");
			System.exit(0);
		} 
		catch (InterruptedException e) 
		{
			System.out.println("Error: Interrupted Exception " +
			"When reading tuple.");
			System.out.println("exiting....");
			System.exit(0);
		}

	}
}
