package org.kidneyomics.hmm;

import static org.junit.Assert.*;

import org.junit.Test;

public class StateSymbolPairCollectionTest {

	@Test
	public void testAdd() {
		StateSymbolPair pair1 = new StateSymbolPair(State.createState("H"), Symbol.createSymbol("S"));
		StateSymbolPair pair2 = new StateSymbolPair(State.createState("T"), Symbol.createSymbol("S2"));
		
		StateSymbolPairCollection collection = new StateSymbolPairCollection();
		
		collection.add(pair1);
		
		assertEquals(1,collection.size());
		
		collection.add(pair2);
		
		assertEquals(2,collection.size());
		
		assertTrue(collection.contains(pair1));
		assertTrue(collection.contains(pair2));
	}

}
