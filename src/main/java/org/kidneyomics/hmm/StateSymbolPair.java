package org.kidneyomics.hmm;

import java.util.Iterator;

public class StateSymbolPair implements Cloneable, Traverseable<StateSymbolPair> {
	private State state;
	private Symbol symbol;
	private StateSymbolPair next = null;
	private StateSymbolPair previous = null;
	
	public StateSymbolPair(State state, Symbol symbol) {
		this.state = state;
		this.symbol = symbol;
	}
	
	public void setNext(StateSymbolPair next) {
		this.next = next;
	}
	
	public StateSymbolPair getNext() {
		return next;
	}
	
	public State getState() {
		return this.state;
	}
	
	public Symbol getEmittedSymbol() {
		return this.symbol;
	}
	
	public Object clone() {
		return new StateSymbolPair(this.state, this.symbol);
	}

	public boolean hasNext() {
		return this.next != null;
	}
	
	@Override
	public String toString() {
		return toString("");
	}
	
	public String toString(String delimiter) {
		StringBuilder seqSb = new StringBuilder();
		seqSb.append("Sequence:\t");
		StringBuilder stateSb = new StringBuilder();
		stateSb.append("States:\t\t");
		
		Iterator<StateSymbolPair> iter = new TraversableIterator<StateSymbolPair>(this);
		
		while(iter.hasNext()) {
			StateSymbolPair next = iter.next();
			seqSb.append(next.getEmittedSymbol());
			stateSb.append(next.getState());
			
			if(iter.hasNext()) {
				seqSb.append(delimiter);
				stateSb.append(delimiter);
			}
			
		}
		
		seqSb.append("\n");
		seqSb.append(stateSb.toString());
		
		return seqSb.toString();
	}

	void setState(State state) {
		this.state = state;
	}
	
	void setSymbol(Symbol symbol) {
		this.symbol = symbol;
	}
	
	public void setPrevious(StateSymbolPair previous) {
		this.previous = previous;
	}

	public StateSymbolPair getPrevious() {
		return previous;
	}

	public boolean hasPrevious() {
		return this.previous != null;
	}
	
}
