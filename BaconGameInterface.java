import java.util.*;
import javax.swing.SwingUtilities;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;

//@author Robert Doherty and Charles Chen, 19W, PS4
public class BaconGameInterface {
	// Maps and graph to hold data
	Map<String, ArrayList<String>> Movietoactor;
	Map<String, String> ActortoID;
	Map<String, String> MovietoID;
	AdjacencyMapGraph<String, HashSet<String>> SocialNetwork;
	
	//Graphlib and string holding current center of universe
	KevinBaconGraphLib graphLib = new KevinBaconGraphLib();
	String center;
	
	//output of bfs
	Graph<String, HashSet<String>> graphLibOutput;

	//constructor
	public BaconGameInterface(String filepath) throws Exception {
		GenerateSocialNetwork();
		ScannerInterface();
	}
	
	//method to generate map of actorIDs to movieIDs with arraylist of actor names as entries
	private void GenerateActortoMovie() throws IOException{
		Movietoactor = new TreeMap<String, ArrayList<String>>();
		BufferedReader input = new BufferedReader(new FileReader("src/movie-actors.txt"));
		String line;
		while ((line = input.readLine()) != null) {
			  String[] pieces = line.split("\\|");
			  if (Movietoactor.containsKey(pieces[0])) {
				  Movietoactor.get(pieces[0]).add(pieces[1]);
			  }
			  else {
				  ArrayList<String> toAdd = new ArrayList<String>();
				  toAdd.add(pieces[1]);
				  Movietoactor.put(pieces[0], toAdd);
			  }
		}
		input.close();
	}
	
	//method to generate map of Actor name to ID
	private void GenerateActortoID() throws IOException{
		ActortoID = new TreeMap<String, String>();
		BufferedReader input = new BufferedReader(new FileReader("src/actors.txt"));
		String line;
		while ((line = input.readLine()) != null) {
			  String[] pieces = line.split("\\|");
			  ActortoID.put(pieces[0], pieces[1]);	  
		}
		input.close();
	}
	
	//method to generate map of movie name to ID
	private void GenerateMovietoID() throws IOException{
		MovietoID = new TreeMap<String, String>();
		BufferedReader input = new BufferedReader(new FileReader("src/movies.txt"));
		String line;
		while ((line = input.readLine()) != null) {
			  String[] pieces = line.split("\\|");
			  MovietoID.put(pieces[0], pieces[1]);	  
		}
		input.close();
	}
	
	//calls all previous methods and generates graph with actor names and movies
	private void GenerateSocialNetwork() throws IOException{
		GenerateActortoMovie();
		GenerateActortoID();
		GenerateMovietoID();
		SocialNetwork = new AdjacencyMapGraph<String, HashSet<String>>();
		
		//Inserts vertex for each actor
		for (String actorID : ActortoID.keySet()) {
			SocialNetwork.insertVertex(ActortoID.get(actorID));
		}
		
		//Inserts edges
		for (String movieID : Movietoactor.keySet()) {
			ArrayList<String> actors = Movietoactor.get(movieID);
			
			//Loops through array of costars and builds edges for between it and all of its neighbors, then removes it from the array and keeps looping until only one name remains
			while (actors.size() > 1) {
				String toAdd = actors.remove(0);

				for (String actorID : actors) {
					if (SocialNetwork.hasEdge(ActortoID.get(toAdd), ActortoID.get(actorID))) {
						SocialNetwork.getLabel(ActortoID.get(toAdd), ActortoID.get(actorID)).add(MovietoID.get(movieID));
					}
					else {
						HashSet<String> edgeToAdd = new HashSet<String>();
						edgeToAdd.add(MovietoID.get(movieID));
						SocialNetwork.insertUndirected(ActortoID.get(toAdd), ActortoID.get(actorID), edgeToAdd);
					}
				}
			}
		}
		
	}
	
	//Methods to handle key input
	//Runs BFS off of graphlib and generates a tree
	private void uCommand(String s) throws Exception {
		graphLibOutput = graphLib.bfs(SocialNetwork, s);
	}
	
	//Finds shortest path, prints out formatted script of each item in the list and returns correct number (size -1)
	private void pCommand(String s) {
		List<String> path = graphLib.getPath(graphLibOutput, s);
		
		System.out.print(s + "'s number is " + (path.size() -1) + "\n");

		for (String v : path) {
			if (v != center) {
				System.out.print(v + " appeared in " + SocialNetwork.getLabel(v, path.get(path.indexOf(v) + 1)) + " with " + path.get(path.indexOf(v) + 1) + "\n");
			}
		}
	}
	
	//prints array of missing vertices disconnected from universe
	private void iCommand() {
		System.out.print(graphLib.missingVertices(SocialNetwork, graphLibOutput));
	}
	
	// prints out list of i best Kevin Bacons by average Separation
	private void cCommand(Integer i) throws Exception {
		
		// creates hashmap of actor names with the avgSeparation as entries
		HashMap<String, Double> avgSep = new HashMap<String, Double>();
		for (String s : ActortoID.keySet()) {
		avgSep.put(ActortoID.get(s), graphLib.averageSeparation(graphLib.bfs(SocialNetwork, ActortoID.get(s)), ActortoID.get(s)));
		}
		
		// Comaprator for strings, compares the avgSeparation between two actor names
		class ActorComparator implements Comparator<String> {
			public int compare(String s1, String s2) {
				if (avgSep.get(s2)- avgSep.get(s1) > 0){
					return -1;
				}
				if (avgSep.get(s2)- avgSep.get(s1) < 0){
					return 1;
				}
				else {
					return 0;
				}
			}
		}
		
		//new comparator and prority queue
		Comparator<String> lenCompare = new ActorComparator();
		PriorityQueue<String>pq = new PriorityQueue<String>(lenCompare);

		//removes strings from priority queue until empty or i = 0
		for (String s : avgSep.keySet()) {
			pq.add(s);
		}
		while (!pq.isEmpty() && i > 0) {
			String s = pq.remove();
			System.out.print(s + ", Avg: " + avgSep.get(s) + "\n");
		i --;
		}

	}
	private void c2Command(Integer l, Integer h) throws Exception {
			
			// creates hashmap of actor names with the avgSeparation as entries
			HashMap<String, Double> avgSep = new HashMap<String, Double>();
			for (String s : ActortoID.keySet()) {
			avgSep.put(ActortoID.get(s), graphLib.averageSeparation(graphLib.bfs(SocialNetwork, ActortoID.get(s)), ActortoID.get(s)));
			}
			
			// Comaprator for strings, compares the avgSeparation between two actor names
			class ActorComparator implements Comparator<String> {
				public int compare(String s1, String s2) {
					if (avgSep.get(s2)- avgSep.get(s1) > 0){
						return -1;
					}
					if (avgSep.get(s2)- avgSep.get(s1) < 0){
						return 1;
					}
					else {
						return 0;
					}
				}
			}
			
			//new comparator and prority queue
			Comparator<String> lenCompare = new ActorComparator();
			PriorityQueue<String>pq = new PriorityQueue<String>(lenCompare);
	
			//removes strings from priority queue until empty or i = 0
			for (String s : avgSep.keySet()) {
				pq.add(s);
			}
			while (!pq.isEmpty()) {
				String s = pq.remove();
				Double avg = avgSep.get(s);
				if (l < avg && avg < h) {
				System.out.print(s + ", Avg: " + avgSep.get(s) + "\n");
				}
			}
	
		}
	// implements simlarly to above method, but instead of comparing avgsep tests degree or # of costars
	private void dCommand(Integer i) {
		class ActorComparator implements Comparator<String> {
			  public int compare(String s1, String s2) {
			    return SocialNetwork.inDegree(s2) - SocialNetwork.inDegree(s1);
			  }
			} 
			Comparator<String> lenCompare = new ActorComparator();
			PriorityQueue<String>pq = new PriorityQueue<String>(lenCompare);

		for (String s : ActortoID.keySet()) {
			pq.add(ActortoID.get(s));
		}
		while (!pq.isEmpty() && i > 0) {
			String s = pq.remove();
			System.out.print(s + ", Degree: " + SocialNetwork.inDegree(s) + "\n");
			i --;
		}
	}
	
	private void d2Command(Integer l, Integer h) {
		class ActorComparator implements Comparator<String> {
			  public int compare(String s1, String s2) {
			    return SocialNetwork.inDegree(s2) - SocialNetwork.inDegree(s1);
			  }
			} 
			Comparator<String> lenCompare = new ActorComparator();
			PriorityQueue<String>pq = new PriorityQueue<String>(lenCompare);

		for (String s : ActortoID.keySet()) {
			pq.add(ActortoID.get(s));
		}
		while (!pq.isEmpty()) {
			String s = pq.remove();
			if (l < SocialNetwork.inDegree(s) && SocialNetwork.inDegree(s) < h) {
				System.out.print(s + ", Degree: " + SocialNetwork.inDegree(s) + "\n");
			}
		}
	}
	// prints avgsep for current center
	private void sCommand() {
		System.out.print(graphLib.averageSeparation(graphLibOutput, center));
		
		/*
		 * Set<String> paths = graphLib.Vertices(SocialNetwork, graphLibOutput); for
		 * (String s : paths) { if (s != center) { Integer avg =
		 * graphLib.averageSeparation(graphLibOutput, center); } }
		 */
	}
	private void wCommand(int i) {
		System.out.println(graphLib.numSeparateFriends(graphLibOutput, center, i));
	}
	
	// Scanner interface for keyboard input, prints rules and calls different commands based on input
	// Has error catching for boundary cases and incorrect input
	private void ScannerInterface() throws Exception{

		Scanner in = new Scanner(System.in);
		Boolean playing = true;
		System.out.print("Commands:\n" + 
		  		"c <#>: list top <#> centers of the universe, sorted by lowest average separation\n" + 
		  		"c2 <low> <high>: list actors sorted by non-infinite separation from the current center, with separation between low and high\n" +
		  		"d <#>: list top <#> actors as centers sorted from highest degree\n" +
		  		"d2 <low> <high>: list actors sorted by degree, with degree between low and high\n" +
		  		"i: list actors with infinite separation from the current center\n" + 
		  		"p <name>: find path from <name> to current center of the universe\n" + 
		  		"s: find the average path length over all actors who are connected by some path to the current center\n" + 
		  		"u <name>: make <name> the center of the universe\n" + 
		  		"q: quit game\n");
		while (playing) {
			  System.out.print(">");
			  String line = in.nextLine();
			  if (line.startsWith("u ")) {
				  String s = line.substring(2);
				  if (SocialNetwork.hasVertex(s)) {
					  center = s;
					  System.out.print(center + " game\n");
					  uCommand(center);
				  }
				  else {
					  System.out.print("no such name\n");
				  }
			  }
			  if (line.startsWith("p ")) {
				  String s = line.substring(2);
				  if (graphLib.missingVertices(SocialNetwork, graphLibOutput).contains(s)) {
					  
					  System.out.print("not connected to center\n");
				  }
				  else if (SocialNetwork.hasVertex(s)) {
					  System.out.print("p <name>: find path from <name> to current center of the universe\n");
					  pCommand(line.substring(2));
				  }
				  else {
					  System.out.print("no such name\n");
				  }
			  }
			  if (line.startsWith("q")) {
				  System.out.print("Game quit!");
				  playing = false;
			  }
			  if (line.startsWith("i")) {
				  if (center == null) {
					  System.out.print("no center inputed");
				  }
				  else {
					  System.out.print("i: list actors with infinite separation from the current center\n");
					  iCommand();
				  }
			  }
			  if (line.startsWith("c ")) {
				  System.out.print("c <#>: list top <#> centers of the universe, sorted by lowest average separation\n");
				  cCommand((Integer.valueOf(line.substring(2))));
			  }
			  if (line.startsWith("c2 ")) {
				  System.out.print("c2 <low> <high>: list actors sorted by non-infinite separation from the current center, with separation between low and high\n");
				  
				  try {
					  c2Command(Integer.valueOf(line.substring(3).split(" ")[0]), Integer.valueOf(line.substring(3).split(" ")[1]));
				  }
				  catch(Exception e) {
					  System.out.print("Incorrect input\n");
				  }
			  }
			  if (line.startsWith("d ")) {
				  System.out.print("d <#>: list top <#> actors as centers sorted from highest degree\n");
				  dCommand((Integer.valueOf(line.substring(2))));
			  }
			  if (line.startsWith("d2 ")) {
				  System.out.print("d2 <low> <high>: list actors sorted by degree, with degree between low and high\n");
				  
				  try {
					  d2Command(Integer.valueOf(line.substring(3).split(" ")[0]), Integer.valueOf(line.substring(3).split(" ")[1]));
				  }
				  catch(Exception e) {
					  System.out.print("Incorrect input\n");
				  }
			  }
			  if (line.startsWith("s")) {
				  if (center == null) {
					  System.out.print("no center inputed");
				  }
				  else {
					  System.out.print("s find the average path length over all actors who are connected by some path to the current center\n");
					  sCommand();
				  }
			  }
			  if (line.startsWith("w ")) {
				  if (center == null) {
					  System.out.print("no center inputed");
				  }
				  else {
					  System.out.print("s find the average path length over all actors who are connected by some path to the current center\n");
					  wCommand(Integer.valueOf(line.substring(2)));
				  }
			  }
		}
	}
	
	public static void main(String[] args) throws Exception {
		BaconGameInterface run = new BaconGameInterface("src/actorsTest.txt");
	}
}
