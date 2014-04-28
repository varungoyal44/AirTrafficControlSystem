/*
 * AirportErrorCheck.java
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
 * actual execution of the airport...
 * 
 * @author Varun Goyal
 *
 */


public class AirportErrorCheck 
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
			return ((Space) registry.lookup(ts));
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

}
