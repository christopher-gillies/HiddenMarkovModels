package org.kidneyomics.hmm;

import static org.junit.Assert.*;

import org.junit.Test;

public class StateTest {

	@Test
	public void testSilent() {
		State silent = State.createState("S");
		
		silent.getTransitions().setProbability(silent, 1);
		
		assertFalse(silent.isValid());
	}

}
