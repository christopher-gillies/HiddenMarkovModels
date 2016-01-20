package org.kidneyomics.hmm;

//TODO: implement
public class TraversableSymbol implements Traverseable<TraversableSymbol> {

	private Symbol symbol;
	
	public TraversableSymbol(Symbol symbol) {
		this.symbol = symbol;
	}
	
	public Symbol getSymbol() {
		return this.symbol;
	}
	
	public void setNext(TraversableSymbol next) {
		// TODO Auto-generated method stub
		
	}

	public TraversableSymbol getNext() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	public String toString(String delimter) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setPrevious(TraversableSymbol previous) {
		// TODO Auto-generated method stub
		
	}

	public TraversableSymbol getPrevious() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasPrevious() {
		// TODO Auto-generated method stub
		return false;
	}

}
