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
		
		// check second column
		ViterbiNode n2 = second.getNode(fair);
		assertEquals(1,n2.getPreviousNodes().size());
		assertEquals(2,n2.getNextNodes().size());
		
		ViterbiNode n3 = second.getNode(biased);
		assertEquals(1,n3.getPreviousNodes().size());
		assertEquals(2,n3.getNextNodes().size());
		
		//check third column
		ViterbiNode n4 = third.getNode(fair);
		assertEquals(2,n4.getPreviousNodes().size());
		assertEquals(2,n4.getNextNodes().size());
		
		ViterbiNode n5 = third.getNode(biased);
		assertEquals(2,n5.getPreviousNodes().size());
		assertEquals(2,n5.getNextNodes().size());
		
		
		//check forth column
		ViterbiNode n6 = forth.getNode(fair);
		assertEquals(2,n6.getPreviousNodes().size());
		assertEquals(2,n6.getNextNodes().size());
		
		ViterbiNode n7 = forth.getNode(biased);
		assertEquals(2,n7.getPreviousNodes().size());
		assertEquals(2,n7.getNextNodes().size());
		
		//check fifth column
		ViterbiNode n8 = fifth.getNode(fair);
		assertEquals(2,n8.getPreviousNodes().size());
		assertEquals(1,n8.getNextNodes().size());
		
		ViterbiNode n9 = fifth.getNode(biased);
		assertEquals(2,n9.getPreviousNodes().size());
		assertEquals(1,n9.getNextNodes().size());
		
		//check sixth column
		ViterbiNode n10 = six.getNode(end);
		assertEquals(2,n10.getPreviousNodes().size());
		
		assertEquals(true,n10.getPreviousNodes().contains(n8));
		assertEquals(true,n10.getPreviousNodes().contains(n9));
		
		assertEquals(0,n10.getNextNodes().size());

	}
	
	@Test
	public void testBuildViterbiGraph2() {
		//TODO: finish test
		Symbol heads = Symbol.createSymbol("H");
		Symbol tails = Symbol.createSymbol("T");
		
		TraversableOrderedSet<TraversableSymbol> emittedSymbols = new TraversableOrderedSet<TraversableSymbol>();
		emittedSymbols.add(new TraversableSymbol(heads));
		emittedSymbols.add(new TraversableSymbol(heads));

		
		State start = State.createStartState();
		
		
		State fair = State.createState("F");
		//set emission probabilities for fair state
		fair.getEmissions().setProbability(heads, 0.5);
		fair.getEmissions().setProbability(tails, 0.5);

		
		State silent = State.createState("S");
		
		//add transitions
		silent.getTransitions().setProbability(silent, 0.5);
		silent.getTransitions().setProbability(fair, 0.5);
		
		assertFalse(silent.isValid());
		
		silent.getTransitions().setProbability(silent, 0.0);
		silent.getTransitions().setProbability(fair, 1.0);
		
		assertTrue(silent.isValid());
		
		
		fair.getTransitions().setProbability(fair, 0.5);
		fair.getTransitions().setProbability(silent, 0.5);
		
		start.getTransitions().setProbability(silent, 1);
		
		HMM hmm = HMM.createHMMFromStartState(start);
		
		ViterbiGraph graph = ViterbiGraph.createViterbiGraphFromHmmAndEmittedSymbols(hmm, emittedSymbols);
		
		assertEquals(4,graph.getColumns().size() );
		
		
		//check first column
		ViterbiColumn first = graph.getColumns().getFirst();
		
		assertEquals(null,first.getSymbol());
		assertEquals(2,first.getNodes().size());
		
		ViterbiNode v0 = first.getNode(start);
		ViterbiNode v1 = first.getNode(silent);
		
		assertEquals(1,v0.getNextNodes().size());
		assertEquals(1,v1.getNextNodes().size());
		
		assertNotNull(v0);
		assertNotNull(v1);
		
		ViterbiColumn second = first.getNext();
		
		assertEquals(2,second.getNodes().size());
		
		ViterbiNode v2 = second.getNode(fair);
		//System.err.println( v2.getNextNodes().get(0).getState());
		//System.err.println( v2.getNextNodes().get(0).getColumn().getColumnNumber());
		
		ViterbiNode v3 = second.getNode(silent);
		
		//
		assertEquals(1,v2.getPreviousNodes().size());
		assertEquals(1,v3.getPreviousNodes().size());
		
		//System.err.println( v3.getPreviousNodes().get(0).getState());
		//System.err.println( v3.getPreviousNodes().get(0).getColumn().getColumnNumber());
		assertTrue(v2.getPreviousNodes().contains(v1));
		assertTrue(v3.getPreviousNodes().contains(v2));
		
		assertEquals(2,v2.getNextNodes().size());
		assertEquals(1,v3.getNextNodes().size());
		
		
		
		
	}
}
