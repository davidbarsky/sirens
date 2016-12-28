package info.rmarcus.dag.permsolve.optimal;

import static org.junit.Assert.*;

import org.junit.Test;

public class StarsAndBarsNodeTest {

	@Test
	public void increasingTest() {
		StarsAndBarsNode sbn = new StarsAndBarsNode(10);
		assertTrue(sbn.partitionSizesIncreasing());
		
		sbn = sbn.getChild(2);
		assertTrue(sbn.partitionSizesIncreasing());
		
		sbn = sbn.getChild(3);
		assertTrue(sbn.partitionSizesIncreasing());
	}
	
	@Test
	public void nonIncreasingTest() {
		StarsAndBarsNode sbn = new StarsAndBarsNode(10);
		assertTrue(sbn.partitionSizesIncreasing());
		
		sbn = sbn.getChild(2);
		assertTrue(sbn.partitionSizesIncreasing());
		
		sbn = sbn.getChild(1);
		assertFalse(sbn.partitionSizesIncreasing());
	}
	
	@Test
	public void nonIncreasingLargeTest() {
		StarsAndBarsNode sbn = new StarsAndBarsNode(1000);
		assertTrue(sbn.partitionSizesIncreasing());
		
		sbn = sbn.getChild(10);
		assertTrue(sbn.partitionSizesIncreasing());
		
		sbn = sbn.getChild(20);
		assertTrue(sbn.partitionSizesIncreasing());
		
		sbn = sbn.getChild(30);
		assertTrue(sbn.partitionSizesIncreasing());
		
		sbn = sbn.getChild(5);
		assertFalse(sbn.partitionSizesIncreasing());
	}

}
