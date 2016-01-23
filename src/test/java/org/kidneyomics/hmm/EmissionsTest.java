package org.kidneyomics.hmm;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class EmissionsTest {
	
	@Test
	public void testEmit() {
		Emissions e = new Emissions(new DefaultRandomNumberSerivce(1l));
		
		Symbol s1 = Symbol.createSymbol("H");
		Symbol s2 = Symbol.createSymbol("T");
		
		e.setProbability(s1, 0.2);
		e.setProbability(s2, 0.8);
		
		assertTrue(e.isValid());
		
		//List<Symbol> emitRes = new LinkedList<Symbol>();
		int count = 0;
		for(int i = 1; i < 100000; i++) {
			//emitRes.add(e.emit());
			Symbol res = e.emit();
			if(res == s1) {
				count++;
			}
		}
		
		System.err.println("T1: Count H " + count);
		
		assertTrue( Math.abs(count - 20000) < 1000 );
	}
	
	
	@Test
	public void testEmit2() {
		Emissions e = new Emissions(new DefaultRandomNumberSerivce(1l));
		
		Symbol s1 = Symbol.createSymbol("H");
		Symbol s2 = Symbol.createSymbol("T");
		Symbol s3 = Symbol.createSymbol("O");
		
		e.setProbability(s1, 0.3);
		e.setProbability(s2, 0.3);
		e.setProbability(s3, 0.4);
		
		assertTrue(e.isValid());
		
		//List<Symbol> emitRes = new LinkedList<Symbol>();
		int s1Count = 0;
		int s2Count = 0;
		int s3Count = 0;
		for(int i = 1; i < 100000; i++) {
			//emitRes.add(e.emit());
			
			Symbol res = e.emit();
			if(res == s1) {
				s1Count++;
			} else if(res == s2) {
				s2Count++;
			} else if(res == s3) {
				s3Count++;
			}
			
		}
		
		System.err.println("T2: Count H " + s1Count);
		System.err.println("T2: Count T " + s2Count);
		System.err.println("T2: Count O " + s3Count);
		
		assertTrue( Math.abs(s1Count - 30000) < 1000 );
		assertTrue( Math.abs(s2Count - 30000) < 1000 );
		assertTrue( Math.abs(s3Count - 40000) < 1000 );
		
	}
	
	//TODO: implement this test, should test contains, add, remove, set 0
	@Test
	public void testThatLogProbsAndProbsMatch() {
		Emissions e = new Emissions(new DefaultRandomNumberSerivce(1l));
		
		Symbol s1 = Symbol.createSymbol("H");
		Symbol s2 = Symbol.createSymbol("T");
		Symbol s3 = Symbol.createSymbol("O");
		
		e.setProbability(s1, 0.3);
		e.setProbability(s2, 0.3);
		e.setProbability(s3, 0.4);
		
		assertTrue(e.isValid());
		
		assertEquals(0.3,Math.exp(e.getLogProbability(s1)),0.0001);
		assertEquals(0.3,Math.exp(e.getLogProbability(s2)),0.0001);
		assertEquals(0.4,Math.exp(e.getLogProbability(s3)),0.0001);
		
		double sum = Math.exp(e.getLogProbability(s1)) + 
				Math.exp(e.getLogProbability(s2)) +
				Math.exp(e.getLogProbability(s3));
		
		assertEquals(1.0, sum, 0.0001);
		
		e.setProbability(s1, 0.0);
		e.setProbability(s2, 0.6);
		
		assertEquals(0.0,Math.exp(e.getLogProbability(s1)),0.0001);
		assertEquals(0.6,Math.exp(e.getLogProbability(s2)),0.0001);
		assertEquals(0.4,Math.exp(e.getLogProbability(s3)),0.0001);
		
		double sum2 = Math.exp(e.getLogProbability(s1)) + 
				Math.exp(e.getLogProbability(s2)) +
				Math.exp(e.getLogProbability(s3));
		
		assertEquals(1.0, sum2, 0.0001);
		
		double sum3 = 
				Math.exp(e.getLogProbability(s2)) +
				Math.exp(e.getLogProbability(s3));
		
		assertEquals(1.0, sum3, 0.0001);
		
		e.remove(s2);
		e.setProbability(s3, 1.0);
		
		double sum4 = 
				Math.exp(e.getLogProbability(s3));
		
		assertEquals(1.0, sum4, 0.0001);
		
		double sum5 = Math.exp(e.getLogProbability(s1)) + 
				Math.exp(e.getLogProbability(s2)) +
				Math.exp(e.getLogProbability(s3));
		
		assertEquals(1.0, sum5, 0.0001);
		
	}
}
