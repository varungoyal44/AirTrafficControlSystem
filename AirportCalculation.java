/*
 * AirportCalculation.java
 * 
 * Version:
 * 	0.1
 * 
 * Revision:
 * 	0.1
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class is to perform ALL the calculations for the airport.
 * like finding the shortest path to other airports.
 * etc.
 * 
 * @author Varun Goyal
 *
 */

public class AirportCalculation 
{
	// Variables...............................................................
	private String name;
	private File configFile;

	private String configArr[][]; // configArr[m][n]...
	private int m, n;
	private int noOfCorridor;

	int routeDistance[][];
	private AirportList sortedAirport[][];

	AirportCorridor corridor[];

	//-------------------------------------------------------------------------
	/**
	 * Constructor to construct the variables.
	 * 
	 * @param	name		name of this airport
	 * 
	 * @param	configFile	the file which contains the configuration 
	 * 						of this airport.
	 */
	AirportCalculation(String name, File configFile)
	{
		this.name = name;
		this.configFile = configFile;
	}


	//-------------------------------------------------------------------------
	/**
	 * to return the value of m...
	 * 
	 * @return	returns the value of m
	 */
	public int getM() 
	{ 
		return m; 
	}

	/**
	 * to get the value of noOfCorridors...
	 * 
	 * @return	returns the value of noOfCorridors...
	 */
	public int getNoOfCorridor() 
	{ 
		return noOfCorridor; 
	}

	/**
	 * to get the corridors for this airport.
	 * 
	 * @return returns the corridors for this airport.
	 */
	public AirportCorridor[] getCorridor() 
	{ 
		return corridor; 
	}

	/**
	 * to get the names of all the valid airports in the network of this
	 * airport.
	 * 
	 * @return		returns the names of all the valid airports 
	 * 				in the network of this airport.
	 */
	public String[] getValidAirports()
	{
		String validAirport[] = new String [m];
		for(int i=0; i<m; i++)
		{
			validAirport[i] = configArr[i][0];
		}

		return validAirport;
	}

	//-------------------------------------------------------------------------
	/**
	 * To check if the file exists and to set the values of m and n
	 *  
	 */
	public void fileErrorCheck(String fileName)
	{
		try 
		{
			BufferedReader br = new BufferedReader (
					new InputStreamReader(
							new FileInputStream(configFile)));

			String line = br.readLine();

			m=0; n=0;
			while(line != null)
			{
				String str_arr[] = line.split(" +"); //to split the input...

				if (n<str_arr.length)
					n=str_arr.length;

				m++;
				line = br.readLine();
			}

			br.close();
			configSyntaxCheck();
		} 
		catch (FileNotFoundException e) 
		{
			System.out.println("Error: "+fileName+" : File not found...");
			System.out.println("exiting...");
			System.exit(0);
		}
		catch (IOException e)
		{
			System.out.println("Error: File read error... IO Exception....");
			System.err.println(e);
			System.out.println("exiting...");
			System.exit(0);
		}
	}


	//-------------------------------------------------------------------------
	/**
	 * this method is to check for the syntax of the of the file...
	 * &
	 * To initialize the ConfigArr array...
	 * 
	 */
	private void configSyntaxCheck()
	{
		configArr = new String [m][n];

		// To initialize the ConfigArr array...
		try 
		{
			BufferedReader br = new BufferedReader (
					new InputStreamReader(
							new FileInputStream(configFile)));

			String line = br.readLine();
			int m=0;
			while(line != null)
			{
				String str_arr[] = line.split(" +"); //to split the input...
				for(int i=0; i<str_arr.length; i++)
					configArr[m][i] = str_arr[i];

				m++;
				line = br.readLine();
			}	
			br.close();
		} catch(Exception e)
		{
			System.err.println(e);
		}

		//Configuration File: Syntax error check...
		try
		{
			for(int i=0; i<m; i++)
			{
				Integer.parseInt(configArr[i][1]);
				Integer.parseInt(configArr[i][2]);
			}
		}
		catch(NumberFormatException e)
		{
			System.out.println("\nError: Configuration File Error:");
			System.out.println("x and y positions are " +
			"not specified properly...");
			System.out.println("exiting....");
			System.exit(0);
		}

		//Configuration File: Symmetrical Corridor Check & 
		//Configuration File: All airport name availability check...
		boolean symChk;
		boolean apNameChk;
		for(int i=0; i<m; i++)
		{
			for(int j=3; j<n; j++)
			{
				symChk = false;
				apNameChk = false;


				for(int k=0; k<m; k++)
				{
					if (configArr[i][j] != null && 
							configArr[i][j].equals(configArr[k][0]))
					{
						apNameChk = true;
						for(int l=3; l<n; l++)
							if (configArr[i][0].equals(configArr[k][l]))
								symChk = true;
					}
				}

				if(apNameChk == false && configArr[i][j] != null)
				{
					System.out.println("\nError: Configuration File Error:");
					System.out.println("For the Airport "+configArr[i][0]+
							" there exists a corridor to "+configArr[i][j]+
							"\n"+
							"However, No airport name "+configArr[i][j]+
					" exists in the given config file.");
					System.out.println("exiting....");
					System.exit(0);
				}


				if(symChk == false && configArr[i][j] != null)
				{
					System.out.println("\nError: Configuration File Error:");
					System.out.println("For the Airport "+configArr[i][0]+
							" there exists a corridor to "+configArr[i][j]+
							"\n"+
							"However, for the Airport "+configArr[i][j]+
							" there is no corridor to "+configArr[i][0] + ".");
					System.out.println("exiting....");
					System.exit(0);
				}
			}
		}
	}



	//-------------------------------------------------------------------------
	/**
	 * To check if the name given in the command line is of a valid airport
	 * according to the config file.
	 * 
	 */
	public void validAirportName()
	{
		boolean nameChk = false;

		for(int i=0; i<m; i++)
			if(configArr[i][0].equals(name))
				nameChk = true;

		if (!nameChk)
		{
			System.out.println("Error: Airport name given in the syntax" +
			"\nAirport doesnot exist in the Configuration File.");
			System.out.println("exiting....");
			System.exit(0);
		}
	}



	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------
	//-------------------------------------------------------------------------
	/**
	 * to perform the calculation of shortest distance between airports...
	 * & this print the result.
	 * 
	 */
	public void calculatePrint()
	{
		// to calculate the number of neighboring corridors...
		for(int i=0; i<m; i++)
		{
			if(configArr[i][0].equals(name))
			{
				for(int j=3; j<n; j++)
				{
					if(configArr[i][j]  != null)
					{
						noOfCorridor++;
					}
				}
			}
		}

		// Initializing Corridor....
		corridor = new AirportCorridor [m-1];
		for(int i=0; i<m-1; i++)
		{
			corridor[i] = new AirportCorridor();
		}

		// Initializing variables...
		routeDistance = new int [m][m];
		intializeRouteDistance();

		// To store the values in a sorted order in the AirportList Class..
		sortedAirport = new AirportList[m][m];
		for(int i=0; i<m; i++)
		{
			for(int j=0; j<m; j++)
			{
				sortedAirport[i][j] = new AirportList(configArr[i][0], configArr[j][0], 
						routeDistance[i][j]);
			}
		}

		// To populate corridor in sortedAirport...
		String via[][] = new String [n][2];
		for(int i=0; i<m; i++)
		{
			int k=0;
			if(configArr[i][0].equals(name))
			{
				for(int j=3; j<n; j++)
					if(configArr[i][j]!=null)
					{
						via[k][0] = configArr[i][j]; 
						k++;
					}
			}
		}

		// To get the print the chart...
		System.out.print("Dest." + "\t" + "Outgoing AirportCorridor");
		boolean encounteredName = false;
		for(int i=0; i<m; i++)
		{
			if (configArr[i][0].equals(name))
				encounteredName = true;

			if(!configArr[i][0].equals(name) && encounteredName == false)
			{
				System.out.println(" ");
				output(configArr[i][0], via, i);
			}
			else if (!configArr[i][0].equals(name) && encounteredName == true)
			{
				System.out.println(" ");
				output(configArr[i][0], via, i-1);
			}	
		}
	}

	//-------------------------------------------------------------------------
	/**
	 * to initialize the RouteDistance array which stores the 
	 * distances of various routes...
	 * 
	 */
	private void intializeRouteDistance ()
	{
		// To get distances between corridor-ed airports...
		for(int i=0; i<m; i++)
		{
			for(int j=0; j<m; j++)
			{
				if(configArr[i][0].equals(configArr[j][0]))
					routeDistance[i][j] = 0;

				else
					routeDistance[i][j] = getDistance(i, j, configArr[j][0], 
							Integer.parseInt(configArr[i][1]), 
							Integer.parseInt(configArr[i][2]));
			}
		}


		// To get distance between all airports...
		Dijkstra();
	}


	//-------------------------------------------------------------------------
	/**
	 * to get the distances between the said two airports...
	 * 
	 * @param	fromPos		tells the position of FROM airport in the 
	 * 						configArr array.
	 * @param	toPos		tells the position of TO airport in the
	 * 						configArr array.
	 * @param	toAirport	tells the name of TO airport.
	 * @param	x			tells the x value.
	 * @param	y			tells the y value.
	 * 
	 */
	private int getDistance(int fromPos, int toPos, String toAirport, 
			int x, int y)
	{
		for(int i=3; i<n; i++)
		{
			if(configArr[fromPos][i] != null && 
					configArr[fromPos][i].equals(toAirport))
			{
				double x1 = Integer.parseInt(configArr[toPos][1]) - x;
				x1 = Math.pow(x1, 2);

				double y1 = Integer.parseInt(configArr[toPos][2]) - y;
				y1 = Math.pow(y1, 2);

				double dist = x1 + y1;
				dist = Math.sqrt(dist);

				return ((int) Math.round(dist));
			}
		}

		return Constant.INFINITY;
	}

	//-------------------------------------------------------------------------
	/**
	 * to set up Dijkstra's graph and get the 
	 * minimum distance between two airports.
	 * 
	 */
	private void Dijkstra()
	{
		Node node[];
		Graph graph;
		Dijkstra dAlg;
		int ret[];

		for (int k=0; k<m; k++)
		{

			// To create Nodes...
			node = new Node[m];
			for(int i=0; i<m; i++)
			{
				node[i] = new Node (configArr[i][0]);
			}

			// To Add outgoing edges...
			for(int i=0; i<m; i++)
			{
				for(int j=0; j<m; j++)
				{
					if(routeDistance[i][j] != 0 || 
							routeDistance[i][j] != Constant.INFINITY)
					{
						node[i].AddOutgoingEdge(node[j], routeDistance[i][j]); 
					}
				}
			}


			// Creating Graph...
			graph = new Graph(node[k]);
			dAlg = new Dijkstra(graph);
			dAlg.go();
			ret = graph.returnDistance(m);

			for(int i=0; i<ret.length; i++)
			{
				if(i<k)
					routeDistance[k][i] = ret[i+1];

				if(i==k)
					routeDistance[k][i] = 0;

				if(i>k)
					routeDistance[k][i] = ret[i];
			}
		}
	}

	//-------------------------------------------------------------------------
	/**
	 * this method calls appropriate methods to prints the output 
	 * i.e. the route for the TO airport, 
	 * and populates the corridor array...
	 * 
	 * @param	to		tells the destination airport.
	 * @param	via		tells via which route the Airplane should 
	 * 					go for the said destination Airport.
	 * @param	pos		position of the corridor to be populated.	
	 * 
	 */
	private void output(String to, String via[][], int pos)
	{
		// Initialize via[][]...
		for(int i=0; i<n; i++)
			via[i][1] = "0";

		// Print output...
		for(int i=0; i<m; i++)
			for(int j=0; j<m; j++)
				for(int k=0; k<n; k++)
					if(via[k][0] != null)
					{
						if(sortedAirport[i][j].from.equals(name) && 
								sortedAirport[i][j].to.equals(via[k][0]))
						{
							via[k][1] = ""+(Integer.parseInt(via[k][1]) + 
									sortedAirport[i][j].dist);
						}

						if(sortedAirport[i][j].from.equals(to) && 
								sortedAirport[i][j].to.equals(via[k][0]))
						{
							via[k][1] = ""+(Integer.parseInt(via[k][1]) + 
									sortedAirport[i][j].dist);
						}
					}

		via = mergeSort(via, 0, n-1);

		corridor[pos].to = to;

		System.out.print(to);
		for(int i=0; i<n; i++)
			if(via[i][0]!=null)
			{
				corridor[pos].via.add(via[i][0]);
				System.out.print("\t"+via[i][0]);
			}
	}

	//-------------------------------------------------------------------------
	/**
	 * to sort the shortest paths in the increasing order.
	 * 
	 * @param	array	denotes the array to be sorted.
	 * @param	lo		gives the lower value of the array.
	 * @param	n		gives the higher value of the array.
	 * 
	 * @return	returns the sorted array.
	 * 
	 */
	private String[][] mergeSort(String array[][],int lo, int n)
	{
		int low = lo;
		int high = n;
		if (low >= high)
		{
			return array;
		}

		int middle = (low + high) / 2;

		mergeSort(array, low, middle);
		mergeSort(array, middle + 1, high);

		int end_low = middle;
		int start_high = middle + 1;

		while ((lo <= end_low) && (start_high <= high)) 
		{
			if (Integer.parseInt(array[low][1]) < Integer.parseInt(
					array[start_high][1])) 
			{
				low++;
			} 
			else 
			{
				String temp[] = array[start_high];
				for (int k = start_high-1; k >= low; k--) 
				{
					array[k+1] = array[k];
				}
				array[low] = temp;
				low++;
				end_low++;
				start_high++;
			}
		}
		return array;
	}

	//-------------------------------------------------------------------------
	/**
	 * the method to instantiate the distance between this airport
	 * and its corridors.
	 * 
	 */
	public void intializeCorridorDist()
	{
		for(int z=0; z<m-1; z++)
			for(int i=0; i<m; i++)
				if(sortedAirport[i][0].from.equals(name))
				{
					for(int j=0; j<m; j++)
						for(int k=0; k<noOfCorridor; k++)
							if(sortedAirport[i][j].to.equals(corridor[z].via.get(k)))
								corridor[z].dist.add(sortedAirport[i][j].dist);
					break;
				}
	}

}
