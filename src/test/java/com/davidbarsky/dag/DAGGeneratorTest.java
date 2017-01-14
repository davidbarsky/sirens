package com.davidbarsky.dag;

import com.davidbarsky.dag.models.TaskQueue;
import com.davidbarsky.schedulers.BoundedScheduler;
import com.davidbarsky.schedulers.RoundRobin;
import info.rmarcus.ggen4j.graph.Vertex;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

public class DAGGeneratorTest {

  @Test
  public void randomGraph() {
    BoundedScheduler roundRobin = new RoundRobin();
    List<TaskQueue> tqs = roundRobin.generateSchedule(4);
    assertEquals(4, tqs.size());
  }

  @Test
  public void generateGraphRange() {
    List<Collection<Vertex>> range = DAGGenerator.generateGraphRange(20);

    // minimum number is 15 vertices, so we should get 6 results back.
    assertTrue(range.size() == 6);
    assertTrue(range.stream().noneMatch(Collection::isEmpty));
  }

  @Test
  public void getErdosGMNSources() {
    Collection<Vertex> sources = DAGGenerator.getErdosGNMSources(20);
    assertTrue(sources.size() == 20);

    sources.forEach(
        vertex -> {
          assertNotNull(vertex);
          assertNotNull(vertex.getParents());
          assertNotNull(vertex.getChildren());
          assertEquals(vertex.hashCode(), Integer.hashCode(vertex.getID()));
        });
  }
}
