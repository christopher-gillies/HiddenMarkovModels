package org.kidneyomics.hmm;

import java.util.ArrayList;
import java.util.List;

public class HMM {
	
	private final State startState;
	//private final Set<State> states;
	//private final Set<Symbol> symbols;
	
	private HMM(State startState) {
		this.startState = startState;
		
		//this.states = new HashSet<State>();
		//this.states.addAll(states);
		
		//this.symbols = new HashSet<Symbol>();
		//this.symbols.addAll(symbols);
	}
	
	
	public static HMM createHMMFromStartState(State startState) {
		return new HMM(startState);
	}
	
	
	public List<StateSymbolPair> generateSequence(int n) {
		List<StateSymbolPair> sequence = new ArrayList<StateSymbolPair>(n);
		
		//set the current state to be the start state
		State current = startState;
		
		//generate a sequence of length n by getting the next state and generating a symbol from it
		//then storing the result in the sequence result
		for(int i = 0; i < n; i++) {
			current = current.emitNextState();
			Symbol emittedSymbol = current.emitSymbol();
			
			StateSymbolPair pair = new StateSymbolPair(current, emittedSymbol);
			sequence.add(pair);
		}
		
		return sequence;
	}
	
	
	
}
