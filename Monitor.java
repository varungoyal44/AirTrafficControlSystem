/*
 * Monitor.java
 * 
 * Version:
 * 		0.1
 * 
 * Revision:
 * 		0.1
 * 
 */

import java.rmi.RemoteException;

import edu.rit.ds.registry.RegistryProxy;
import edu.rit.ds.space.Space;

/**
 * The monitor program prints the ATCS's current state as specified below.
 * Whenever the ATCS's state changes, the monitor program prints the ATCS's 
 * new state as specified below. ATCS state changes occur whenever an airplane starts, 
 * whenever an airplane leaves one corridor and enters another corridor, 
 * and whenever an airplane lands. 
 * 
 * 
 * USAGE:
 *     java Monitor <host> <port> <ts>
 *     
 * WHERE:
 * <host> is the name of the host computer where the Registry Server is running.
 * 
 * <port> is the port number to which the Registry Server is listening. 
 * 
 * <ts>   is the name of the tuple space bound into the Registry Server.
 * 
 * @author Varun Goyal
 *
 */

public class Monitor 
{
	//-------------------------------------------------------------------------
	String host;
	int port;
	String ts;
	
	private RegistryProxy registry;
	private Space space;
	
	//-------------------------------------------------------------------------
	/**
	 * This is the constructor to construct the variables and check for 
	 * exceptions in the program.
	 * 
	 * @param	args[]	gets the command line arguments from the user.
	 * 
	 */
	Monitor(String args[]) throws RemoteException
	{
		// To check if any argument is missing or 
		// Extra arguments are passed...
		argumentCheck(args);

		// To set the variable...
		host = args[0];
		ts = args[2];

		// The port argument cannot be parsed as an integer.
		try 
		{ 	
			port = Integer.parseInt(args[1]); 
		}
		catch(NumberFormatException e)
		{
			System.out.println("Invalid port number...");
			System.out.println("exiting...");
			System.exit(0);
		}


		// To check for registry server...
		try
		{
			registry = new RegistryProxy (host, port);
			space = (Space) registry.lookup(ts);
			printMonitor();
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
			System.out.println("Error: Not Bound Exception " +
			"When Connecting to the Registry server.");
			System.out.println("exiting....");
			System.exit(0);
		}

	}

	//-------------------------------------------------------------------------
	/**
	 * this method checks for irregularities in the command line arguments.
	 * 
	 * @param	args[]	gets the command line arguments from the user.
	 */
	private void argumentCheck(String args[])
	{
		// default argument:
		// java Monitor <host> <port> <ts>
		int len = args.length;

		if(len < 3)
		{
			System.out.println("Error: Argument Missing...");
			System.out.println("Default: " +
			"java Monitor <host> <port> <ts>");
			System.out.println("exiting...");
			System.exit(0);
		}

		if(len > 3)
		{
			System.out.println("Error: Extra Argument provided...");
			System.out.println("Default: " +
			"java Monitor <host> <port> <ts>");
			System.out.println("exiting...");
			System.exit(0);
		}
	}

	//-------------------------------------------------------------------------
	/**
	 * The main method.
	 * 
	 * @param		args[]				gets the command line 
	 * 									arguments from the user.
	 * 
	 * @exception	RemoteException		to throw the remote exception.		
	 * 
	 */
	public static void main(String args[]) throws RemoteException
	{
		for(int i=0; i<80; i++)
			System.out.print("*");

		System.out.println("");

		new Monitor(args);
	}

	//-------------------------------------------------------------------------
	/**
	 * Method to be bound to the Registry. Implementation of the 
	 * Monitor Interface.
	 * To print the current status of the ATCS on the screen.
	 * 
	 * @param		msg					the message to be printed. 
	 * 									
	 * 
	 * @exception	RemoteException		throws the remote exception.
	 * 		
	 */
	public void printMonitor() 
	{
		Object[] monitorTemplate = new Object[] {"Monitor", null};	// String msg
		Object[] monitorTuple = null;
		
		while(true)
		{
			try 
			{
				monitorTuple = space.take(monitorTemplate);
				System.out.println(monitorTuple[1]);
				
			} 
			catch (RemoteException e) 
			{	
				e.printStackTrace();
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
			catch (NullPointerException e) 
			{
				System.out.println("Null pointer");
			}
		}
	}
}
//--------------------------------XXX------------------------------------------