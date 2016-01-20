package org.kidneyomics.hmm;

import java.util.HashMap;
import java.util.Map;

class ViterbiColumn implements Nextable<ViterbiColumn> {
	
	private final Map<State,ViterbiNode> nodes;
	private final int columnNumber;
	private ViterbiColumn next;
	private final Symbol symbol;
	
	ViterbiColumn(int columnNumber, Symbol symbol) {
		this.columnNumber = columnNumber;
		this.nodes = new HashMap<State,ViterbiNode>();
		this.symbol = symbol;
	}
	
	ViterbiColumn(int columnNumber) {
		this.columnNumber = columnNumber;
		this.nodes = new HashMap<State,ViterbiNode>();
		this.symbol = null;
	}



	public void setNext(ViterbiColumn next) {
		this.next = next;
	}



	public ViterbiColumn getNext() {
		return next;
	}



	public boolean hasNext() {
		if(this.next != null) {
			return true;
		} else {
			return false;
		}
	}



	public String toString(String delimter) {
		// TODO Auto-generated method stub
		return null;
	}

	public ViterbiNode addNode(ViterbiNode node) {
		node.setColumn(this);
		return this.nodes.put(node.getState(), node);
	}

	public ViterbiNode getNode(State state) {
		return this.nodes.get(state);
	}


	public int getColumnNumber() {
		return columnNumber;
	}



	public Symbol getSymbol() {
		return symbol;
	}

	
	
	
}
