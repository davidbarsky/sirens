package com.davidbarsky.schedulers;

import com.davidbarsky.dag.TopologicalSorter;
import com.davidbarsky.dag.models.Task;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class EdgeZeroTest {
    private List<Task> graph;
    private EdgeZero scheduler;

    @Before
    public void init() {
        graph = TopologicalSorter.generateGraph(20);
        scheduler = new EdgeZero();
    }

    @Test
    public void findBLevel() throws Exception {
        Map<Task, Integer> leveledGraph = scheduler.findBLevel(graph, 1);
        // Sanity Checks
        leveledGraph.forEach((task, integer) -> {
            assertNotNull(integer);
            assertNotNull(task);
        });

        // B-levels must consider computation costs.
        boolean noZeroValuesPresent = leveledGraph.values()
                .stream()
                .noneMatch(i -> i == 0);
        assertTrue(noZeroValuesPresent);
    }

    @Test
    public void findTLevel() throws Exception {
        Map<Task, Integer> leveledGraph = scheduler.findTLevel(graph, 1);
        // Sanity Checks
        leveledGraph.forEach((task, integer) -> {
            assertNotNull(integer);
            assertNotNull(task);
        });

        List<Integer> zeroValues = leveledGraph.values().stream()
                .filter(i -> i == 0)
                .collect(Collectors.toList());
        assertTrue("At least 1 node in the graph can have a T-level of 0, with no upper bound.",
                zeroValues.size() >= 1);
    }

    @Test
    public void generateSchedule() throws Exception {
        fail("Not implemented yet.");
    }
}
