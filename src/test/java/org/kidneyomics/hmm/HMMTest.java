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
	public void testDiscoverStates1() {
		
		HMM hmm = createBiasedCoinHMM();
		
		System.err.println("States: " + hmm.getStateNames().size());
		assertTrue(2 == hmm.getStateNames().size());
		assertTrue(hmm.getStateByName("F") != null);
		assertTrue(hmm.getStateByName("B") != null);

		assertTrue(2 == hmm.getSymbolNames().size());
		assertTrue(hmm.getSymbolByName("T") != null);
		assertTrue(hmm.getSymbolByName("H") != null);
		
	}
	
	@Test
	public void validateGeneratedSequenceLength() {
		
		HMM hmm = createBiasedCoinHMM();
		
		List<StateSymbolPair> pairs = hmm.generateSequence(100).getAsList();
		
		assertEquals(100,pairs.size());
		
		System.err.println(StateSymbolPair.createVisualRepresentation(pairs, ""));
	}
	
	@Test
	public void testCalcProbOfSymbolGivenStateProbs() {
		HMM hmm = createBiasedCoinHMM();
		
		Map<State,Double> probs = new HashMap<State,Double>();
		
		State fair = hmm.getStateByName("F");
		State biased = hmm.getStateByName("B");
		
		Symbol heads = hmm.getSymbolByName("H");
		Symbol tails = hmm.getSymbolByName("T");
		
		probs.put(fair, 0.1);
		probs.put(biased, 0.9);
		
		double resHeads = hmm.calcProbOfSymbolGivenStateProbs(heads, probs);
		
		double expResHeads = 0.1 * 0.5 + 0.9 * 0.9;
		
		System.err.println("Exp Result Heads: " + expResHeads);
		System.err.println("Result Heads: " + resHeads);
		assertEquals(expResHeads,resHeads,0.000001);
		
		
		double resTails = hmm.calcProbOfSymbolGivenStateProbs(tails, probs);
		
		double expResTails = 0.1 * 0.5 + 0.9 * 0.1;
		
		System.err.println("Exp Result Tails: " + expResTails);
		System.err.println("Result Tails: " + resTails);
		assertEquals(expResTails,resTails,0.000001);
		
		assertEquals(1.0,resTails + resHeads, 0.000001);
	}

	
}
