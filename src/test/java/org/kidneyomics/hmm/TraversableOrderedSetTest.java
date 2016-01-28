package org.kidneyomics.hmm;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.ListIterator;

import org.junit.Test;

public class TraversableOrderedSetTest {

	@Test
	public void testAdd() {
		StateSymbolPair pair1 = new StateSymbolPair(State.createState("H"), Symbol.createSymbol("S"));
		StateSymbolPair pair2 = new StateSymbolPair(State.createState("T"), Symbol.createSymbol("S2"));
		
		TraversableOrderedSet<StateSymbolPair> collection = new TraversableOrderedSet<StateSymbolPair>();
		
		collection.add(pair1);
		
		assertEquals(1,collection.size());
		
		collection.add(pair2);
		
		assertEquals(2,collection.size());
		
		assertTrue(collection.contains(pair1));
		assertTrue(collection.contains(pair2));
		
		assertFalse(collection.add(pair1));
		assertFalse(collection.add(pair2));
		
		assertEquals(pair1,collection.getFirst());
		assertEquals(pair2,collection.getLast());
	}
	
	@Test
	public void testIterator() {
		StateSymbolPair pair1 = new StateSymbolPair(State.createState("H"), Symbol.createSymbol("S"));
		StateSymbolPair pair2 = new StateSymbolPair(State.createState("T"), Symbol.createSymbol("S2"));
		
		StateSymbolPair pair3 = new StateSymbolPair(State.createState("H"), Symbol.createSymbol("S"));
		StateSymbolPair pair4 = new StateSymbolPair(State.createState("T"), Symbol.createSymbol("S2"));
		
		
		TraversableOrderedSet<StateSymbolPair>  collection = new TraversableOrderedSet<StateSymbolPair>();
		
		assertTrue(collection.add(pair1));
		assertTrue(collection.add(pair2));
		assertTrue(collection.add(pair3));
		assertTrue(collection.add(pair4));
		
		assertEquals(4,collection.size());
		
		Iterator<StateSymbolPair> iter = collection.iterator();
		int count = 0;
		while(iter.hasNext()) {
			StateSymbolPair next = iter.next();
			switch(count) {
			case 0:
				System.err.println("Checking pair1");
				assertEquals(pair1,next);
				assertEquals(pair2,pair1.getNext());
				break;
			case 1:
				System.err.println("Checking pair2");
				assertEquals(pair2,next);
				assertEquals(pair3,pair2.getNext());
				break;
			case 2:
				System.err.println("Checking pair3");
				assertEquals(pair3,next);
				assertEquals(pair4,pair3.getNext());
				break;
			case 3:
				System.err.println("Checking pair4");
				assertEquals(pair4,next);
				assertEquals(null,pair4.getNext());
				break;
				default:
					fail();
			}
			count++;
			
		}
		
		assertEquals(4,count);
		
	}
	
	
	@Test
	public void testBackwards() {
		StateSymbolPair pair1 = new StateSymbolPair(State.createState("H"), Symbol.createSymbol("S"));
		StateSymbolPair pair2 = new StateSymbolPair(State.createState("T"), Symbol.createSymbol("S2"));
		
		StateSymbolPair pair3 = new StateSymbolPair(State.createState("H"), Symbol.createSymbol("S"));
		StateSymbolPair pair4 = new StateSymbolPair(State.createState("T"), Symbol.createSymbol("S2"));
		
		
		TraversableOrderedSet<StateSymbolPair>  collection = new TraversableOrderedSet<StateSymbolPair>();
		
		assertTrue(collection.add(pair1));
		assertTrue(collection.add(pair2));
		assertTrue(collection.add(pair3));
		assertTrue(collection.add(pair4));
	
		ListIterator<StateSymbolPair> iter = collection.tailIterator();
		int count = 0;
		StateSymbolPair item = null;
		// null -> 1 -> 2 -> 3 -> 4
		while(iter.hasPrevious()) {
			item = iter.previous();
			count++;
		}
		
		assertEquals(4,count);
		assertEquals(pair1,item);
	}
	
	@Test
	public void testIteratorForwardThenBackwards() {
		StateSymbolPair pair1 = new StateSymbolPair(State.createState("H"), Symbol.createSymbol("S"));
		StateSymbolPair pair2 = new StateSymbolPair(State.createState("T"), Symbol.createSymbol("S2"));
		
		StateSymbolPair pair3 = new StateSymbolPair(State.createState("H"), Symbol.createSymbol("S"));
		StateSymbolPair pair4 = new StateSymbolPair(State.createState("T"), Symbol.createSymbol("S2"));
		
		
		TraversableOrderedSet<StateSymbolPair>  collection = new TraversableOrderedSet<StateSymbolPair>();
		
		assertTrue(collection.add(pair1));
		assertTrue(collection.add(pair2));
		assertTrue(collection.add(pair3));
		assertTrue(collection.add(pair4));
	
		ListIterator<StateSymbolPair> iter = collection.iterator();
		int count = 0;
		StateSymbolPair item = null;
		// null -> 1 -> 2 -> 3 -> 4
		while(iter.hasNext()) {
			item = iter.next();
			count++;
		}
		assertEquals(4,count);
		assertEquals(pair4,item);
		iter.next(); 
		// null -> 1 -> 2 -> 3 -> 4 -> null
		//null -> 4 -> 3 -> 2 -> 1
		while(iter.hasPrevious()) {
			item = iter.previous();
			count--;
		}
		assertEquals(0,count);
		assertEquals(pair1,item);
		//null -> 4 -> 3 -> 2 -> 1 -> null
		iter.previous();
		// null -> 1 -> 2 -> 3 -> 4
		while(iter.hasNext()) {
			item = iter.next();
			count++;
		}
		assertEquals(4,count);
		assertEquals(pair4,item);
		
	}
	
	@Test
	public void testPrevious() {
		StateSymbolPair pair1 = new StateSymbolPair(State.createState("H"), Symbol.createSymbol("S"));
		StateSymbolPair pair2 = new StateSymbolPair(State.createState("T"), Symbol.createSymbol("S2"));
		
		StateSymbolPair pair3 = new StateSymbolPair(State.createState("H"), Symbol.createSymbol("S"));
		StateSymbolPair pair4 = new StateSymbolPair(State.createState("T"), Symbol.createSymbol("S2"));
		
		
		TraversableOrderedSet<StateSymbolPair>  collection = new TraversableOrderedSet<StateSymbolPair>();
		
		assertTrue(collection.add(pair1));
		assertTrue(collection.add(pair2));
		assertTrue(collection.add(pair3));
		assertTrue(collection.add(pair4));
		
		
		StateSymbolPair last = collection.getLast();
		assertEquals(pair4,last);
		assertEquals(pair3,last.getPrevious());
		assertEquals(pair2,last.getPrevious().getPrevious());
		assertEquals(pair1,last.getPrevious().getPrevious().getPrevious());
		assertEquals(null,last.getPrevious().getPrevious().getPrevious().getPrevious());
	}

}
