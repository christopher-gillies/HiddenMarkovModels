package org.kidneyomics.hmm;

//TODO: implement
public class TraversableSymbol implements Traverseable<TraversableSymbol> {

	private Symbol symbol;
	private TraversableSymbol next;
	private TraversableSymbol previous;
	public TraversableSymbol(Symbol symbol) {
		this.symbol = symbol;
	}
	
	public Symbol getSymbol() {
		return this.symbol;
	}
	
	public void setNext(TraversableSymbol next) {
		this.next = next;
		
	}

	public TraversableSymbol getNext() {
		return this.next;
	}

	public boolean hasNext() {
		return this.next != null;
	}

	public String toString(String delimter) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setPrevious(TraversableSymbol previous) {
		this.previous = previous;
		
	}

	public TraversableSymbol getPrevious() {
		return this.previous;
	}

	public boolean hasPrevious() {
		return this.previous != null;
	}

}
