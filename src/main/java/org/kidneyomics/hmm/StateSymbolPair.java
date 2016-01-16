package org.kidneyomics.hmm;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class StateSymbolPair {
	private State state;
	private Symbol symbol;
	private StateSymbolPair next = null;
	
	public List<StateSymbolPair> getAsList() {
		List<StateSymbolPair> res = new LinkedList<StateSymbolPair>();
		
		//add first
		StateSymbolPair current = this;
		res.add(current);
		
		//add rest
		while(current.getNext() != null) {
			//get next
			StateSymbolPair next = current.getNext();
			res.add(next);
			//make current = to next
			current = next;
		}
		
		return res;
	}
	
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
	
	public static String createVisualRepresentation(List<StateSymbolPair> pairs, String delimiter) {
		StringBuilder seqSb = new StringBuilder();
		seqSb.append("Sequence:\t");
		StringBuilder stateSb = new StringBuilder();
		stateSb.append("States:\t\t");
		
		Iterator<StateSymbolPair> iter = pairs.iterator();
		
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
}
