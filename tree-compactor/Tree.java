package ps3;


import java.security.InvalidKeyException;
import java.util.*;

/**
 * Generic binary search tree
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 * @author CBK, Fall 2016, min
 * @author Robert Doherty
 */

public class Tree{
	private Character key;
	private Integer value;
	private Tree left, right;

	public Integer getValue() {
		return value;
	}
	
	public Character getKey() {
		return key;
	}
	
	public Tree getRight() {
		return right;
	}
	
	public Tree getLeft() {
		return left;
	}
	
	/**
	 * Constructs leaf node -- left and right are null
	 */
	public Tree(Character key, Integer value) {
		this.key = key; this.value = value;
	}

	/**
	 * Constructs inner node
	 */
	public Tree(Character key, Integer value, Tree left, Tree right) {
		this.key = key; this.value = value;
		this.left = left; this.right = right;
	}

	/**
	 * Is it a leaf node?
	 */
	public boolean isLeaf() {
		return left == null && right == null;
	}

	/**
	 * Does it have a left child?
	 */
	public boolean hasLeft() {
		return left != null;
	}

	/**
	 * Does it have a right child?
	 */
	public boolean hasRight() {
		return right != null;
	}

	/**
	 * Returns the value associated with the search key, or throws an exception if not in BST
	 */
	public Integer find(Character search) throws InvalidKeyException {
		System.out.println(key); // to illustrate
		int compare = search.compareTo(key);
		if (compare == 0) return value;
		if (compare < 0 && hasLeft()) return left.find(search);
		if (compare > 0 && hasRight()) return right.find(search);
		throw new InvalidKeyException(search.toString());
	}

	/**
	 * Smallest key in the tree, recursive version
	 */
	public Character min() {
		if (left != null) return left.min();
		return key;
	}

	/**
	 * Smallest key in the tree, iterative version
	 */
	public Character minIter() {
		Tree curr = this;
		while (curr.left != null) curr = curr.left;
		return curr.key;
	}

	/**
	 * Inserts the key & value into the tree (replacing old key if equal)
	 */
	public void insert(Character key, Integer value) {
		int compare = key.compareTo(this.key);
		if (compare == 0) {
			// replace
			this.value = value;
		}
		else if (compare < 0) {
			// insert on left (new leaf if no left)
			if (hasLeft()) left.insert(key, value);
			else left = new Tree(key, value);
		}
		else if (compare > 0) {
			// insert on right (new leaf if no right)
			if (hasRight()) right.insert(key, value);
			else right = new Tree(key, value);
		}
	}

	/**
	 * Deletes the key and returns the modified tree, which might not be the same object as the original tree
	 * Thus must afterwards just use the returned one
	 */
	public Tree delete(Character search) throws InvalidKeyException {
		int compare = search.compareTo(key);
		if (compare == 0) {
			// Easy cases: 0 or 1 child -- return other
			if (!hasLeft()) return right;
			if (!hasRight()) return left;
			// If both children are there, delete and substitute the successor (smallest on right)
			// Find it
			Tree successor = right;
			while (successor.hasLeft()) successor = successor.left;
			// Delete it
			right = right.delete(successor.key);
			// And take its key & value
			this.key = successor.key;
			this.value = successor.value;
			return this;
		}
		else if (compare < 0 && hasLeft()) {
			left = left.delete(search);
			return this; 
		}
		else if (compare > 0 && hasRight()) {
			right = right.delete(search);
			return this;
		}
		throw new InvalidKeyException(search.toString());
	}

	/**
	 * Parenthesized representation:
	 * <tree> = "(" <tree> ["," <tree>] ")" <key> ":" <value>
	 *        | <key> ":" <value>
	 */
	public String toString() {
		if (isLeaf()) return key+":"+value;
		String s = "(";
		if (hasLeft()) s += left;
		else s += "_";
		s += ",";
		if (hasRight()) s += right;
		else s += "_";
		return s + ")" + key+":"+value;
	}
}

