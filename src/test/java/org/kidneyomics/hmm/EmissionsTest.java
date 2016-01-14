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
		
		System.err.println(count);
		
		assertTrue( Math.abs(count - 20000) < 2000 );
	}
}
