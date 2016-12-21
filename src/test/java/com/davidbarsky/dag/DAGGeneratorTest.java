package com.davidbarsky.dag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;

import org.junit.Test;

import com.davidbarsky.dag.models.TaskQueue;
import com.davidbarsky.schedulers.RoundRobin;

import info.rmarcus.ggen4j.graph.Vertex;

public class DAGGeneratorTest {

	@Test
	public void randomGraph() {
		List<TaskQueue> tqs = RoundRobin.invoke(4);
		assertEquals(4, tqs.size());
	}

	@Test
	public void generateGraphRange() {
		List<Collection<Vertex>> range = DAGGenerator.generateGraphRange(20);

		assertTrue(range.size() == 20);
		assertTrue(range.stream().noneMatch(Collection::isEmpty));
	}

	@Test
	public void getErdosGMNSources() {
		Collection<Vertex> sources = DAGGenerator.getErdosGNMSources(20);
		assertTrue(sources.size() == 20);

		sources.forEach(vertex -> {
			assertNotNull(vertex);
			assertNotNull(vertex.getParents());
			assertNotNull(vertex.getChildren());
			assertEquals(vertex.hashCode(), Integer.hashCode(vertex.getID()));
		});
	}

}
