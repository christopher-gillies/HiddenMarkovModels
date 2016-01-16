package org.kidneyomics.hmm;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class HMMTest {

	public HMM createBiasedCoinHMM() {
		Symbol heads = Symbol.createSymbol("H");
		Symbol tails = Symbol.createSymbol("T");
		
		State start = State.createStartState();
		
		
		State fair = State.createState("F");
		//set emission probabilities for fair state
		fair.getEmissions().setProbability(heads, 0.5);
		fair.getEmissions().setProbability(tails, 0.5);
		
		
		
		State biased = State.createNamedStartState("B");
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
	public void testDiscoverStates1() {
		Symbol heads = Symbol.createSymbol("H");
		Symbol tails = Symbol.createSymbol("T");
		
		State start = State.createStartState();
		
		
		State fair = State.createState("F");
		//set emission probabilities for fair state
		fair.getEmissions().setProbability(heads, 0.5);
		fair.getEmissions().setProbability(tails, 0.5);
		
		
		
		State biased = State.createNamedStartState("B");
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
		
		assertTrue(2 == hmm.getStates().size());
		assertTrue(hmm.getStates().contains(fair));
		assertTrue(hmm.getStates().contains(biased));

	}
	
	@Test
	public void validateGeneratedSequenceLength() {
		
		HMM hmm = createBiasedCoinHMM();
		
		List<StateSymbolPair> pairs = hmm.generateSequence(100);
		
		assertEquals(100,pairs.size());
		
		System.err.println(StateSymbolPair.createVisualRepresentation(pairs, ""));
	}
	
	@Test
	public void testCalcProbOfSymbolGivenStateProbs() {
		HMM hmm = createBiasedCoinHMM();
		
		Map<State,Double> probs = new HashMap<State,Double>();
		
		//probs.put(key, value)
		
		
	}

	
}
