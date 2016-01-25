package org.kidneyomics.hmm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Enumerator<T> implements Iterable<List<T>> {
	
	/*
	 * TODO: use binary tree to construct all symbol combinations
	 * 
	 * input wil be a list of states and each level will correspond to the
	 * length of enumeration
	 */
	public static <T> Enumerator<T> getEnumeratorForSymbolsAndLength(Set<T> symbols, int length) {

		return new Enumerator<T>(symbols,length);
	}
	
	private Enumerator(Set<T> symbols, int length) {
		this.symbols = symbols;
		this.length = length;
		this.size = symbols.size();
		int num = (int) Math.pow(size, length);
		this.leaves = new ArrayList<Node>( num );
		buildTree();
	}
	
	private final int length;
	private final Set<T> symbols;
	private final int size;
	private final List<Node> leaves;
	
	private void buildTree() {
		Node root = new Node(null,size);
		root.level = 0;
		addChildren(root);
	}
	
	public int size() {
		return this.leaves.size();
	}
	
	private void addChildren(Node parent) {
		if(parent.level == length) {
			this.leaves.add(parent);
			return;
		} else {
			int index = 0;
			for(T t : symbols) {
				Node child = new Node(t,size);
				child.symbol = t;
				child.parent = parent;
				child.level = parent.level + 1;
				parent.children.add(index, child);
				index++;
				addChildren(child);
			}
		}
	}
	
	private class Node {
		
		Node(T symbol, int size) {
			this.symbol = symbol;
			this.children = new ArrayList<Node>(size);
		}
		
		int level;
		T symbol;
		Node parent = null;
		List<Node> children = null;
		
	}

	public Iterator<List<T>> iterator() {
		return new PermutationIterator(this.leaves);
	}
	
	private class PermutationIterator implements Iterator<List<T>> {

		Iterator<Node> iter = null;
		PermutationIterator(List<Node> leaves) {
			iter = leaves.iterator();
		}
		
		public boolean hasNext() {
			return iter.hasNext();
		}

		public List<T> next() {
			
			//get the next leaf of the tree
			Node current = iter.next();
			
			LinkedList<T> result = new LinkedList<T>();
			
			//until we get to the root of the tree
			while(current.parent != null) {
				result.addFirst(current.symbol);
				current = current.parent;
			}
			
			return result;
			
		}

		public void remove() {
			// TODO Auto-generated method stub
			
		}
		
	}
}
