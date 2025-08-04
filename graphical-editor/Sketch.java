import java.util.TreeMap;

public class Sketch {
	private TreeMap<Integer, Shape> shapes;			// Map to track shapes
	private Integer i = 0;							// ID tracker
	
	public Sketch() {
		this.shapes = new TreeMap<Integer, Shape>();
	}
	
	// adds new shape to map
	public void addShape(Shape toAdd) {
		this.shapes.put(i, toAdd);
		i += 1;
	}
	
	// returns the map
	public TreeMap<Integer, Shape> getShapes(){
		return this.shapes;
	}
	
	//checks contains by looping through each item in map and calling contains on the shapes
	public Integer contains(int x, int y) {
		for (Integer shapeKey :shapes.descendingKeySet()) {
			if (shapes.get(shapeKey).contains(x, y)) return shapeKey;
		}
		return null;
	}
	
	// removes a given key
	public void remove(Integer shapeKey) {
		shapes.remove(shapeKey);
	}
	
	// sets the global index
	public void setIndex(Integer i) {
		this.i = i;
	}
	
	// returns global index
	public Integer getIndex() {
		return i;
	}
}
