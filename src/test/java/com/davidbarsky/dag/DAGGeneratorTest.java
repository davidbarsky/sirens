package com.davidbarsky.dag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;

import org.junit.Test;

import com.davidbarsky.dag.models.TaskQueue;
import com.davidbarsky.schedulers.RoundRobin;

import info.rmarcus.ggen4j.graph.Vertex;


class DAGGeneratorTest {

	@Test
	void randomGraph() {
		List<TaskQueue> tqs = RoundRobin.invoke(4);
		assertEquals(4, tqs.size());

	}

	@Test
	void getErdosGMNSources() {
		Collection<Vertex> sources = DAGGenerator.getErdosGNMSources();
		assertTrue(sources.size() != 0);


		sources.forEach(vertex -> {
			assertTrue(vertex != null);
			assertTrue(vertex.getParents() != null);
			assertTrue(vertex.getChildren() != null);
			assertEquals(vertex.hashCode(), Integer.hashCode(vertex.getID()));
		});
	}

}
