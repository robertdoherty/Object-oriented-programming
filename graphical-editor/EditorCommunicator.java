import java.awt.Color;
import java.awt.Point;
import java.io.*;
import java.net.Socket;

/**
 * Handles communication to/from the server for the editor
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 * @author Chris Bailey-Kellogg; overall structure substantially revised Winter 2014
 * @author Travis Peters, Dartmouth CS 10, Winter 2015; remove EditorCommunicatorStandalone (use echo server for testing)
 */
public class EditorCommunicator extends Thread {
	private PrintWriter out;		// to server
	private BufferedReader in;		// from server
	protected Editor editor;		// handling communication for

	/**
	 * Establishes connection and in/out pair
	 */
	public EditorCommunicator(String serverIP, Editor editor) {
		this.editor = editor;
		System.out.println("connecting to " + serverIP + "...");
		try {
			Socket sock = new Socket(serverIP, 4242);
			out = new PrintWriter(sock.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			System.out.println("...connected");
		}
		catch (IOException e) {
			System.err.println("couldn't connect");
			System.exit(-1);
		}
	}

	/**
	 * Sends message to the server
	 */
	public void send(String msg) {
		out.println(msg);
	}

	/**
	 * Keeps listening for and handling (your code) messages from the server
	 */
	public void run() {
		try {
			// Handle messages
			// TODO: YOUR CODE HERE
			String line;
			while ((line = in.readLine()) != null) {
				System.out.println(line);
				
				// when receiving ADD commands splits the incoming string and sends the editor commmands to add the given shape to the sketch
				if (line.startsWith("ADD ellipse ")){
					System.out.println("Adding ellipse!");
					String[] ellipse = line.substring(12).split(" ");
					editor.getSketch().addShape(new Ellipse(Integer.valueOf(ellipse[0]), Integer.valueOf(ellipse[1]), Integer.valueOf(ellipse[2]), Integer.valueOf(ellipse[3]), new Color(Integer.valueOf(ellipse[4]))));
					editor.repaint();
				}
				// to move, the communicator commands the editor to update the position of the given ID and the movefrom variable
				if (line.startsWith("MOVE ")){
					System.out.println("Moving!");
					String[] beginmove = line.substring(5).split(" ");
					editor.getSketch().getShapes().get(editor.getMovingID()).moveBy(Integer.valueOf(beginmove[0])-(int)editor.getMoveFrom().getX(), Integer.valueOf(beginmove[1])-(int)editor.getMoveFrom().getY());
					editor.setMoveFrom(new Point(Integer.valueOf(beginmove[0]), Integer.valueOf(beginmove[1])));
					editor.repaint();
				}	
				// sets a movingId and moveFrom when a move begins
				if (line.startsWith("BEGINMOVE ")){
					System.out.println("Beginning move!");
					String[] beginmove = line.substring(10).split(" ");
					editor.setMovingID(Integer.valueOf(beginmove[0]));
					editor.setMoveFrom(new Point(Integer.valueOf(beginmove[1]), Integer.valueOf(beginmove[2])));
				}
				// to end the move movefrom is set to null and the canvas is repainted
				if (line.startsWith("ENDMOVE ")){
					System.out.println("Ending move!");
					editor.setMoveFrom(null);
					editor.repaint();
				}
				// the following methods deal with updating a new client on the current sketch status
				// for each shape, it is put with the correct ID into the sketch map (not just added) and the index for adding new shapes
				if(line.startsWith("UPDATE ellipse ")){
					System.out.println("Updating!");
					String[] updateEllipse = line.substring(15).split(" ");
					editor.getSketch().getShapes().put(Integer.valueOf(updateEllipse[5]), new Ellipse(Integer.valueOf(updateEllipse[0]), Integer.valueOf(updateEllipse[1]), Integer.valueOf(updateEllipse[2]), Integer.valueOf(updateEllipse[3]), new Color(Integer.valueOf(updateEllipse[4]))));
					editor.getSketch().setIndex(Integer.valueOf(updateEllipse[6]));
					editor.repaint();
				}
				if(line.startsWith("UPDATE rectangle ")){
					System.out.println("Updating!");
					String[] updateRectangle = line.substring(17).split(" ");
					editor.getSketch().getShapes().put(Integer.valueOf(updateRectangle[5]), new Rectangle(Integer.valueOf(updateRectangle[0]), Integer.valueOf(updateRectangle[1]), Integer.valueOf(updateRectangle[2]), Integer.valueOf(updateRectangle[3]), new Color(Integer.valueOf(updateRectangle[4]))));
					editor.getSketch().setIndex(Integer.valueOf(updateRectangle[6]));
					editor.repaint();
				}
				if(line.startsWith("UPDATE segment ")){
					System.out.println("Updating!");
					String[] updateSegment = line.substring(15).split(" ");
					editor.getSketch().getShapes().put(Integer.valueOf(updateSegment[5]), new Segment(Integer.valueOf(updateSegment[0]), Integer.valueOf(updateSegment[1]), Integer.valueOf(updateSegment[2]), Integer.valueOf(updateSegment[3]), new Color(Integer.valueOf(updateSegment[4]))));
					editor.getSketch().setIndex(Integer.valueOf(updateSegment[6]));
					editor.repaint();
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			System.out.println("server hung up");
		}
	}	

	// Send editor requests to the server
	// TODO: YOUR CODE HERE
	
}
