package ps3;

import java.util.Comparator;
/**
 * @author Robert Doherty, PS3, 19W
 * 
 * @param a
 * @param b
 * @return
 */

public class TreeComparator implements Comparator<Tree>{

	@Override
	public int compare(Tree x, Tree y) {
		// TODO Auto-generated method stub
		if(x.getValue() > y.getValue()) {
			return 1;
		}
		if(x.getValue() < y.getValue()) {
			return -1;
		}
		return 0;
	}
}
