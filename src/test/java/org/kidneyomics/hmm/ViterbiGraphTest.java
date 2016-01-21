package org.kidneyomics.hmm;

import static org.junit.Assert.*;

import org.junit.Test;

public class ViterbiGraphTest {

	public HMM createBiasedCoinHMM() {
		Symbol heads = Symbol.createSymbol("H");
		Symbol tails = Symbol.createSymbol("T");
		
		State start = State.createStartState();
		
		
		State fair = State.createState("F");
		//set emission probabilities for fair state
		fair.getEmissions().setProbability(heads, 0.5);
		fair.getEmissions().setProbability(tails, 0.5);
		
		
		
		State biased = State.createState("B");
		//set emission probabilities for biased state
		biased.getEmissions().setProbability(heads, 0.9);
		biased.getEmissions().setProbability(tails, 0.1);
		
		//equal chance to be in fair or biased state
		start.getTransitions().setProbability(fair, 0.5);
		start.getTransitions().setProbability(biased, 0.5);
		
		fair.getTransitions().setProbability(fair, 0.9);
		fair.getTransitions().setProbability(biased, 0.1);
		
		biased.getTransitions().setProbability(fair, 0.1);
		biased.getTransitions().setProbability(biased, 0.9);
		
		HMM hmm = HMM.createHMMFromStartState(start);
		
		
		return hmm;
	}
	
	@Test
	public void testBuildViterbiGraph1() {
		HMM hmm = createBiasedCoinHMM();
		Symbol heads = hmm.getSymbolByName("H");
		Symbol tails = hmm.getSymbolByName("T");
		State start = hmm.getStartState();
		State end = hmm.getEndState();
		State fair = hmm.getStateByName("F");
		State biased = hmm.getStateByName("B");
		
		TraversableOrderedSet<TraversableSymbol> emittedSymbols = new TraversableOrderedSet<TraversableSymbol>();
		emittedSymbols.add(new TraversableSymbol(heads));
		emittedSymbols.add(new TraversableSymbol(heads));
		emittedSymbols.add(new TraversableSymbol(heads));
		emittedSymbols.add(new TraversableSymbol(tails));
		
		ViterbiGraph graph = ViterbiGraph.createViterbiGraphFromHmmAndEmittedSymbols(hmm, emittedSymbols);
		
		assertEquals(6,graph.getColumns().size());
		
		ViterbiColumn firstCol = graph.getColumns().getFirst();
		assertEquals(null,firstCol.getSymbol());
		assertEquals(-1,firstCol.getColumnNumber());
		
		//Check number of states per column, symbol and index
		assertEquals(1,firstCol.getNodes().size());
		
		ViterbiColumn second = firstCol.getNext();
		assertEquals(2,second.getNodes().size());
		assertEquals(heads,second.getSymbol());
		assertEquals(0,second.getColumnNumber());
		
		ViterbiColumn third = second.getNext();
		assertEquals(2,third.getNodes().size());
		assertEquals(heads,third.getSymbol());
		assertEquals(1,third.getColumnNumber());
		
		ViterbiColumn forth = third.getNext();
		assertEquals(2,forth.getNodes().size());
		assertEquals(heads,forth.getSymbol());
		assertEquals(2,forth.getColumnNumber());
		
		ViterbiColumn fifth = forth.getNext();
		assertEquals(2,fifth.getNodes().size());
		assertEquals(tails,fifth.getSymbol());
		assertEquals(3,fifth.getColumnNumber());
		
		ViterbiColumn six = fifth.getNext();
		assertEquals(1,six.getNodes().size());
		assertEquals(null,six.getSymbol());
		assertEquals(4,six.getColumnNumber());
		
		//check transitions for first column
		ViterbiNode n1 = firstCol.getNode(start);
		assertEquals(0,n1.getPreviousNodes().size());
		assertEquals(2,n1.getNextNodes().size());

		boolean containsFair = false;
		boolean containsBiased = false;
		for(ViterbiNode node : n1.getNextNodes()) {
			if(node.getState() == fair) {
				containsFair = true;
			}
			
			if(node.getState() == biased) {
				containsBiased = true;
			}
		}
		
		assertTrue(containsFair);
		assertTrue(containsBiased);
		
		ViterbiNode n2 = second.getNode(fair);
		assertEquals(2,n2.getPreviousNodes().size());
		assertEquals(2,n1.getNextNodes().size());
		
	}
}
