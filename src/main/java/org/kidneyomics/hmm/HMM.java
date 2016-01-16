package org.kidneyomics.hmm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kidneyomics.hmm.State.VISIT_LEVEL;

public class HMM {
	
	private final State startState;
	private final Set<State> states;
	//private final Set<Symbol> symbols;
	
	private HMM(State startState) {
		this.startState = startState;
		this.states = new HashSet<State>();
		discoverStates(this.startState);
		//this.states = new HashSet<State>();
		//this.states.addAll(states);
		
		//this.symbols = new HashSet<Symbol>();
		//this.symbols.addAll(symbols);
	}
	
	
	public static HMM createHMMFromStartState(State startState) {
		if(!startState.isStartState()) {
			throw new IllegalArgumentException("Please input a start state");
		}
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
	
	public double calcProbOfSymbolGivenStateProbs(Symbol symbol, Map<State,Double> map) {
		/*
		 * Marginalize the joint distribution of p(symbol,state)
		 * 
		 * --> conditional probability <--
		 * p(symbol,state) = p(symbol|state) * p(state);
		 * sum_{states} p(symbol,state) = p(symbol) by the law of total probability
		 * 
		 */
		double prob = 0;
		for(Map.Entry<State, Double> entry : map.entrySet()) {
			double probOfState = entry.getValue();
			double probOfSymbol = entry.getKey().getEmissions().getProbability(symbol);
			prob += probOfSymbol * probOfState;
		}
		return prob;
	}
	
	
	private void discoverStates(State state) {
		if(state.getVisitLevel() == VISIT_LEVEL.CLOSED) {
			return;
		} else {
			state.setVisitLevel(VISIT_LEVEL.CLOSED);
			
			List<State> statesToVisit = new LinkedList<State>();
			
			for(State s : state.getTransitions().getKeys()) {
				//only add states not yet visited to stateToVisit list
				// i.e. only visit states not yet visited
				if(s.getVisitLevel() == VISIT_LEVEL.NOT_VISITED) {
					s.setVisitLevel(VISIT_LEVEL.VISITED);
					states.add(s);
					statesToVisit.add(s);
				}
			}
			
			// now go to each state that was not previously visited
			for(State s : statesToVisit) {
				discoverStates(s);
			}
			
			
		}
	}
	
	Set<State> getStates() {
		return this.states;
	}
	
	
}
