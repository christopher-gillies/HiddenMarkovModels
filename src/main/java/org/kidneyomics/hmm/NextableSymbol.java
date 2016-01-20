package org.kidneyomics.hmm;

public class NextableSymbol implements Nextable<NextableSymbol> {

	private Symbol symbol;
	
	public NextableSymbol(Symbol symbol) {
		this.symbol = symbol;
	}
	
	public Symbol getSymbol() {
		return this.symbol;
	}
	
	public void setNext(NextableSymbol next) {
		// TODO Auto-generated method stub
		
	}

	public NextableSymbol getNext() {
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

}
