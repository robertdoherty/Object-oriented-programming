

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

//@author Robert Doherty and Charles Chen, 19W, PS4
public class KevinBaconGraphLib {	
	/**
	 * returns path tree leading to source
	 * @param Social Network
	 * @param source
	 * @return path tree
	 * @throws Exception
	 */
	public static <V,E> Graph<V,E> bfs(Graph<V,E> g, V source) throws Exception {
		Graph<V,E> bfs = new AdjacencyMapGraph<V,E>();
		SLLQueue<V> queue = new SLLQueue<V>();
		Map<V, HashSet<V>> paths = new HashMap<V, HashSet<V>>();
		// add start root to queue and graph
		queue.enqueue(source);
		bfs.insertVertex(source);
		
		while(!queue.isEmpty()) { //until queue is empty
			V v = queue.dequeue();
			for(V neighbor : g.outNeighbors(v)) {
				// if graph does not already contain vertex add it to queue, path, and graph
				if(!bfs.hasVertex(neighbor)) {
					if(!paths.containsKey(v))  paths.put(v,new HashSet<V>());
					paths.get(v).add(neighbor);
					queue.enqueue(neighbor);
					bfs.insertVertex(neighbor);
				}
			}
		}
		// create all the edges
		for(V v : paths.keySet()) {
			for(V i : paths.get(v)) {
				bfs.insertDirected(i, v, null);
			}
		}
		return bfs;
	} 
	
	/**
	 * gets a path from a vertex to the center given a tree
	 * @param tree
	 * @param v
	 * @return outlist if tree has vertex v, otherwise null
	 */
		public static <V,E> List<V> getPath(Graph<V,E> tree, V v) {
			List<V> shortestPath = new LinkedList<V>();
			if (tree.hasVertex(v)){
				V current = v; 
				while (tree.outDegree(current) != 0) { // iterate through all the out degrees and add vertex to list
					shortestPath.add(current);
					current = tree.outNeighbors(current).iterator().next();
				}
				shortestPath.add(current);
				return shortestPath;
			}
			return null;
		}
		/**
		 * Goes through the vertices in the tree, if v is not in the subtree, it's missing (add to set)
		 * @param graph
		 * @param subgraph
		 * @return missing - missing vertices set
		 */
		public static <V,E> Set<V> missingVertices(Graph<V,E> graph, Graph<V,E> subgraph) {
			Set<V> missing = new HashSet<>();
			for (V v: graph.vertices()) {
				if (!subgraph.hasVertex(v)) missing.add(v); // if not in graph add it to the set
			}
			return missing;
			}
		public static <V,E> Set<V> Vertices(Graph<V,E> graph, Graph<V,E> subgraph) {
			Set<V> missing = new HashSet<>();
			for (V v: graph.vertices()) {
				if (subgraph.hasVertex(v)) missing.add(v); // if not in graph add it to the set
			}
			return missing;
			}
		/**
		 * Recursively finds the average distance of a shortest path tree.
		 * iterates through and adds them up
		 * divides by array list size to get average separation
		 * @param tree
		 * @param root
		 * @return
		 */
		public static <V,E> double averageSeparation(Graph<V,E> tree, V root) {
			List<Double> distances = new ArrayList<Double>();
			addAllDistances(tree,distances, root, 1.0);
			double total = 0;
			for (Double d: distances) {
				total += d;
			}
			return total/distances.size();
			
		}
		/** Helper method for averageSeparation
		 * recurses through graph adding distances
		*/
		public static <V,E> void addAllDistances(Graph<V,E> tree, List<Double> list, V vertex, Double d) {
			for (V v: tree.inNeighbors(vertex)){
				list.add(d);
				addAllDistances(tree, list, v, d + 1);
			}
		}
		public static <V,E> int numSeparateFriends(Graph<V,E> bfs, V center, int numSteps) {
			if (numSteps == 0) return 1;
			int total = 0;
			System.out.println(bfs.outDegree(center));
			System.out.println(bfs.inDegree(center));
			for (V nbr : bfs.inNeighbors(center)) {
					System.out.println(nbr);
			        total += numSeparateFriends(bfs, nbr, numSteps-1);
			      }
			      return total;
//			Integer amigos = 0;
//			System.out.println(numSteps);
//			System.out.println(center);
//			if (numSteps ==1) {
//				for (V out : bfs.inNeighbors(center)) amigos +=1;
//			}
//			else {
//				for (V out: bfs.outNeighbors(center)) numSeparateFriends(bfs, out, numSteps -1);	
//				}
//			return amigos;
		}
			public static void main(String[] args) throws Exception {
				Graph<String, String> graph = new AdjacencyMapGraph<String, String>();
				graph.insertVertex("Kevin Bacon");
				graph.insertVertex("Bob");
				graph.insertVertex("Alice");
				graph.insertVertex("Charlie");

				graph.insertUndirected("Kevin Bacon", "Bob", "A Movie");
				graph.insertUndirected("Kevin Bacon", "Alice", "A Movie");
				graph.insertUndirected("Kevin Bacon", "Alice", "E Movie");
				graph.insertUndirected("Alice", "Bob", "A Movie");
				graph.insertUndirected("Alice", "Charlie", "D Movie");
				graph.insertUndirected("Bob", "Charlie", "C Movie");

				Graph<String, String> bfs = bfs(graph, "Charlie");
				System.out.println(bfs);
				Integer amigos = numSeparateFriends(bfs, "Kevin Bacon", 1);
				System.out.println(amigos);
			}
	}
	
