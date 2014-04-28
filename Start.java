//******************************************************************************
//
// File:    Start.java
// Package: ---
// Unit:    Class Start
//
// This Java source file is copyright (C) 2006 by Alan Kaminsky. All rights
// reserved. For further information, contact the author, Alan Kaminsky, at
// ark@cs.rit.edu.
//
// This Java source file is part of the Computer Science Course Library ("The
// Library"). The Library is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 3 of the License, or (at your
// option) any later version.
//
// The Library is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
// FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
// details.
//
// A copy of the GNU General Public License is provided in the file gpl.txt. You
// may also obtain a copy of the GNU General Public License on the World Wide
// Web at http://www.gnu.org/licenses/gpl.html.
//
//******************************************************************************

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import java.util.Vector;

/**
 * Class Start is a remote object launcher main program. The Start program
 * creates one or more objects which are instances of classes specified on the
 * command line. The Start program maintains active references to these objects
 * so they won't be garbage collected. After creating all the objects, the Start
 * program blocks itself so it never exits. The Start program must be terminated
 * externally (e.g., by hitting the interrupt key or killing the process).
 * <P>
 * To create a single object, type this command:
 * <P>
 * <TT>java Start</TT> <I>classname</I> [<I>args</I>]
 * <P>
 * where <I>classname</I> is the fully-qualified name of the class to
 * instantiate and <I>args</I> is an optional list of command line arguments,
 * separated by whitespace. The Start program finds the given class and looks
 * for a public constructor with one argument of type <TT>String[]</TT> (array
 * of strings). The Start program invokes that constructor, passing in an array
 * of zero or more strings, one string for each command line argument. Before
 * invoking the constructor, the Start program prints an informational message.
 * If the Start program cannot find the given class, if it cannot find the
 * requisite constructor, or if the constructor throws any exception, the Start
 * program prints an error message and terminates.
 * <P>
 * The Start program can create more than one object, all of which run in the
 * same Java Virtual Machine (JVM). To create further objects, specify
 * additional class names and optional command line arguments, preceding each
 * class name with a plus sign (<TT>+</TT>). For example, to create two objects,
 * type this command:
 * <P>
 * <TT>java Start</TT> <I>classname</I> [<I>args</I>] <TT>+</TT>
 * <I>classname</I> [<I>args</I>]
 * <P>
 * To create three objects, type this command:
 * <P>
 * <TT>java Start</TT> <I>classname</I> [<I>args</I>] <TT>+</TT>
 * <I>classname</I> [<I>args</I>] <TT>+</TT> <I>classname</I> [<I>args</I>]
 * <P>
 * And so on.
 * <P>
 * If any of the objects being started requires a security manager, include the
 * <TT>"-Djava.security.policy=&lt;policyfile&gt;"</TT> flag on the Java command
 * line. If this Java system property is defined, the Start program will install
 * an instance of class java.lang.SecurityManager as the security manager. You
 * must also define the security policy in the file named
 * <TT>"&lt;policyfile&gt;"</TT>.
 *
 * @author  Alan Kaminsky
 * @version 12-Dec-2006
 */
public class Start
	{

// Prevent construction.

	private Start()
		{
		}

// Exported operations.

	/**
	 * Main program.
	 */
	public static void main
		(String[] args)
		{
		try
			{
			int i = 0;
			int j = 0;
			int n = 0;
			Class theClass = null;
			Constructor theConstructor = null;
			String[] theArgList = null;

			// Make sure there are some arguments.
			if (args.length == 0)
				{
				usage();
				}

			// Install a security manager if necessary.
			String policyfile = System.getProperty ("java.security.policy");
			if (policyfile != null && System.getSecurityManager() == null)
				{
				System.out.println
					("Setting a security manager, java.security.policy=" +
					 policyfile);
				System.setSecurityManager (new SecurityManager());
				}

			// Vector of constructors (type Constructor) for objects.
			Vector theConstructors = new Vector();

			// Vector of argument lists (type String[]) for objects.
			Vector theArgLists = new Vector();

			// For looking for constructors.
			Class[] theConstructorArgTypes = new Class[] {args.getClass()};

			// Process command line, building up constructors and argument
			// lists.
			while (i < args.length)
				{
				// Get class.
				try
					{
					theClass = Class.forName (args[i]);
					}
				catch (ClassNotFoundException exc)
					{
					System.err.println
						("Start: Cannot find class \"" + args[i] + "\"");
					System.exit (1);
					}

				// Get constructor.
				try
					{
					theConstructor =
						theClass.getConstructor (theConstructorArgTypes);
					}
				catch (NoSuchMethodException exc)
					{
					System.err.println
						("Start: Cannot find constructor " + args[i] + "(java.lang.String[])");
					System.exit (1);
					}

				// Get command line arguments.
				j = i + 1;
				i = j;
				while (i < args.length && ! args[i].equals ("+"))
					{
					++ i;
					}
				n = i - j;
				theArgList = new String [n];
				System.arraycopy (args, j, theArgList, 0, n);

				// Record constructor and command line arguments.
				theConstructors.add (theConstructor);
				theArgLists.add (theArgList);

				// If we hit a plus sign, make sure there are more arguments.
				if (i == args.length)
					{
					// No more arguments.
					}
				else if (i < args.length-1)
					{
					// Plus sign and more arguments. Skip over plus sign.
					++ i;
					}
				else
					{
					// Plus sign but no more arguments. Error.
					usage();
					}
				}

			// Vector of objects.
			Vector theObjects = new Vector();

			// Instantiate all objects.
			n = theConstructors.size();
			for (i = 0; i < n; ++ i)
				{
				theConstructor = (Constructor) theConstructors.elementAt (i);
				theArgList = (String[]) theArgLists.elementAt (i);

				// Print an informational message.
				System.out.print ("Creating ");
				System.out.print (theConstructor.getName());
				System.out.print ("({");
				for (j = 0; j < theArgList.length; ++ j)
					{
					if (j > 0)
						{
						System.out.print (',');
						}
					System.out.print ('"');
					System.out.print (theArgList[j]);
					System.out.print ('"');
					}
				System.out.println ("})");

				// Invoke constructor.
				theObjects.add
					(theConstructor.newInstance
						(new Object[] {theArgList}));
				}

			// Block forever.
			Thread.currentThread().join();
			}

		catch (InvocationTargetException exc)
			{
			System.err.println ("Start: Uncaught exception");
			exc.getCause().printStackTrace (System.err);
			System.exit (1);
			}

		catch (Throwable exc)
			{
			System.err.println ("Start: Uncaught exception");
			exc.printStackTrace (System.err);
			System.exit (1);
			}
		}

// Hidden operations.

	/**
	 * Print a usage message and exit.
	 */
	private static void usage()
		{
		System.err.println
			("Usage: java Start <classname> [<args>] [ + <classname> [<args>] ... ]");
		System.exit (1);
		}

	}
