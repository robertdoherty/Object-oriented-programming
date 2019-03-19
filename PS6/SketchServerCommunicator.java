import java.awt.Color;
import java.awt.Point;
import java.io.*;
import java.net.Socket;

/**
 * Handles communication between the server and one client, for SketchServer
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012; revised Winter 2014 to separate SketchServerCommunicator
 */
public class SketchServerCommunicator extends Thread {
	private Socket sock;					// to talk with client
	private BufferedReader in;				// from client
	private PrintWriter out;				// to client
	private SketchServer server;			// handling communication for

	public SketchServerCommunicator(Socket sock, SketchServer server) {
		this.sock = sock;
		this.server = server;
	}

	/**
	 * Sends a message to the client
	 * @param msg
	 */
	public void send(String msg) {
		out.println(msg);
	}
	
	/**
	 * Keeps listening for and handling (your code) messages from the client
	 */
	public void run() {
		try {
			System.out.println("someone connected");
			
			// Communication channel
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintWriter(sock.getOutputStream(), true);

			// Tell the client the current state of the world
			// TODO: YOUR CODE HERE
		
			// Keep getting and handling messages from the client
			// TODO: YOUR CODE HERE
			String line;
			
			// handles catching up a new editor, sends UPDATE messages for each shape in the master sketch
			for (Integer shapeKey: server.getSketch().getShapes().descendingKeySet()) {
				out.println("UPDATE " + server.getSketch().getShapes().get(shapeKey).toString() + " " + shapeKey + " " + server.getSketch().getIndex());
				}
			while ((line = in.readLine()) != null) {
				System.out.println(line);
				
				// broadcasts line with command to all clients
				server.broadcast(line);
				System.out.println(line);
				
				//handles adding shapes/recoloring/deleting to the master sketch, identical in operation to how EditorCommunicator adds shapes
				if (line.startsWith("ADD ellipse ")){
					System.out.println("Adding ellipse!");
					String[] ellipse = line.substring(12).split(" ");
					server.getSketch().addShape(new Ellipse(Integer.valueOf(ellipse[0]), Integer.valueOf(ellipse[1]), Integer.valueOf(ellipse[2]), Integer.valueOf(ellipse[3]), new Color(Integer.valueOf(ellipse[4]))));
				}
				if (line.startsWith("ADD rectangle ")){
					System.out.println("Adding rectangle!");
					String[] rectangle = line.substring(14).split(" ");
					server.getSketch().addShape(new Rectangle(Integer.valueOf(rectangle[0]), Integer.valueOf(rectangle[1]), Integer.valueOf(rectangle[2]), Integer.valueOf(rectangle[3]), new Color(Integer.valueOf(rectangle[4]))));
				}
				if (line.startsWith("ADD segment ")){
					System.out.println("Adding segment!");
					String[] segment = line.substring(12).split(" ");
					server.getSketch().addShape(new Segment(Integer.valueOf(segment[0]), Integer.valueOf(segment[1]), Integer.valueOf(segment[2]), Integer.valueOf(segment[3]), new Color(Integer.valueOf(segment[4]))));
				}
				if (line.startsWith("RECOLOR ")){
					System.out.println("Recoloring!");
					String[] recolor = line.substring(8).split(" ");
					server.getSketch().getShapes().get(Integer.valueOf(recolor[0])).setColor(new Color(Integer.valueOf(recolor[1])));
				}
				if (line.startsWith("DELETE ")){
					System.out.println("Deleting!");
					String[] delete = line.substring(7).split(" ");
					server.getSketch().remove(Integer.valueOf(delete[0]));
				}

				// the server needs only the final position for each move to store (doesnt have to show the move to viewer) so it only sends an update of the final move
				if (line.startsWith("ENDMOVE ")){
					System.out.println("Ending move!");
					String[] endmove = line.substring(8).split(" ");
					server.getSketch().getShapes().get(Integer.valueOf(endmove[0])).moveBy(Integer.valueOf(endmove[1]), Integer.valueOf(endmove[2]));

				}

			}
			// Clean up -- note that also remove self from server's list so it doesn't broadcast here
			server.removeCommunicator(this);
			out.close();
			in.close();
			sock.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
