package org.kidneyomics.hmm;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class EmissionsTest {
	
	@Test
	public void testEmit() {
		Emissions e = new Emissions();
		Symbol s1 = new Symbol("H");
		Symbol s2 = new Symbol("T");
		
		e.addSymbol(s1, 0.2);
		e.addSymbol(s2, 0.8);
		
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
		Emissions e = new Emissions();
		Symbol s1 = new Symbol("H");
		Symbol s2 = new Symbol("T");
		Symbol s3 = new Symbol("O");
		
		e.addSymbol(s1, 0.3);
		e.addSymbol(s2, 0.3);
		e.addSymbol(s3, 0.4);
		
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
}
