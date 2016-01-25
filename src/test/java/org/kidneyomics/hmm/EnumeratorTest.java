package org.kidneyomics.hmm;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.kidneyomics.hmm.Enumerator;
import org.kidneyomics.hmm.Symbol;

public class EnumeratorTest {

	@Test
	public void testSize() {
		Symbol heads = Symbol.createSymbol("H");
		Symbol tails = Symbol.createSymbol("T");
		
		HashSet<Symbol> symbols = new HashSet<Symbol>();
		symbols.add(heads);
		symbols.add(tails);
		
		Enumerator<Symbol> enumerator = Enumerator.getEnumeratorForSymbolsAndLength(symbols, 4);
		
		assertEquals(16, enumerator.size());
	}
	
	@Test
	public void testElements() {
		Symbol heads = Symbol.createSymbol("H");
		Symbol tails = Symbol.createSymbol("T");
		
		HashSet<Symbol> symbols = new HashSet<Symbol>();
		symbols.add(heads);
		symbols.add(tails);
		
		Enumerator<Symbol> enumerator = Enumerator.getEnumeratorForSymbolsAndLength(symbols, 4);
		
		assertEquals(16, enumerator.size());
		
		int count = 0;
		Iterator<List<Symbol>> iter = enumerator.iterator();
		while(iter.hasNext()) {
			System.err.println(iter.next());
			count++;
		}
		
		assertEquals(16, count);
	}

}
