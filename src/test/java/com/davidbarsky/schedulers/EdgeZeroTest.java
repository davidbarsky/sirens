package com.davidbarsky.schedulers;

import com.davidbarsky.dag.DAGException;
import com.davidbarsky.dag.TopologicalSorter;
import com.davidbarsky.dag.models.Task;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class EdgeZeroTest {
  @Test
  public void findBLevel() throws Exception {
    List<Task> graph = TopologicalSorter.generateGraph(20);
    EdgeZero scheduler = new EdgeZero();

    Map<Task, Integer> leveledGraph = scheduler.findBLevel(graph, 1);
    // Sanity Checks
    leveledGraph.forEach((task, integer) -> {
      assertNotNull(integer);
      assertNotNull(task);
    });

    // At most 1 node should have a t-level of 0.
    Integer zeroValue = leveledGraph.values().stream()
            .filter(i -> i == 0)
            .findFirst()
            .orElseThrow(DAGException::new);

    assertEquals(new Integer(0), zeroValue);
  }

  @Test
  public void findTLevel() throws Exception {
    List<Task> graph = TopologicalSorter.generateGraph(20);
    EdgeZero scheduler = new EdgeZero();

    Map<Task, Integer> leveledGraph = scheduler.findTLevel(graph, 1);
    // Sanity Checks
    leveledGraph.forEach((task, integer) -> {
      assertNotNull(integer);
      assertNotNull(task);
    });

    // At most 1 node should have a t-level of 0.
    Integer zeroValue = leveledGraph.values().stream()
            .filter(i -> i == 0)
            .findFirst()
            .orElseThrow(DAGException::new);

    assertEquals(new Integer(0), zeroValue);
  }

  @Test
  public void generateSchedule() throws Exception {
    fail("Not implemented yet.");
  }
}
