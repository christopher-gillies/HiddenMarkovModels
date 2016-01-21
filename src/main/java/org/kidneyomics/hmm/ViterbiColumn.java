package org.kidneyomics.hmm;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class ViterbiColumn implements Traverseable<ViterbiColumn> {
	
	//nodes are stored by state
	private final Map<State,ViterbiNode> nodes;
	private final int columnNumber;
	private ViterbiColumn next;
	private ViterbiColumn previous;
	private final Symbol symbol;
	private final boolean isLastColumn;
	private final boolean isFirstColumn;
	
	private ViterbiColumn(int columnNumber, Symbol symbol, boolean isLastColumn, boolean isFirstColumn) {
		this.columnNumber = columnNumber;
		this.nodes = new HashMap<State,ViterbiNode>();
		this.symbol = symbol;
		this.isLastColumn = isLastColumn;
		this.isFirstColumn = isFirstColumn;
	}
	
	static ViterbiColumn createLastColumn(int columnNumber) {
		return new ViterbiColumn(columnNumber, null, true, false);
	}
	
	static ViterbiColumn createFirstColumn() {
		return new ViterbiColumn(-1, null, false, true);
	}
	
	static ViterbiColumn createInteriorColumn(int columnNumber, Symbol symbol) {
		return new ViterbiColumn(columnNumber, symbol, false, false);
	}
	
	boolean isInteriorColumn() {
		return !isFirstColumn && !isLastColumn;
	}

	boolean isLastColumn() {
		return isLastColumn;
	}

	boolean isFirstColumn() {
		return isFirstColumn;
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

	Collection<ViterbiNode> getNodes() {
		return this.nodes.values();
	}

	public int getColumnNumber() {
		return columnNumber;
	}



	public Symbol getSymbol() {
		return symbol;
	}

	public void setPrevious(ViterbiColumn previous) {
		this.previous = previous;
	}

	public ViterbiColumn getPrevious() {
		return previous;
	}

	public boolean hasPrevious() {
		return this.previous != null;
	}

	
	
	
}
