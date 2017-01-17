package com.davidbarsky.schedulers;

import org.junit.Test;

import com.davidbarsky.dag.TopologicalSorter;
import com.davidbarsky.dag.models.Task;
import org.junit.Before;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

public class GraphPropertiesTest {
    private List<Task> graph;

    @Before
    public void init() {
        graph = TopologicalSorter.generateGraph(15);
    }

    @Test
    public void findBottomLevelForGraph() throws Exception {
        Map<Task, Integer> leveledGraph = GraphProperties.findBottomLevel(graph, 1);
        // Sanity Checks
        leveledGraph.forEach((task, integer) -> {
            assertNotNull(integer);
            assertNotNull(task);
        });

        // B-levels must consider computation costs, so zero-values are *not* possible.
        boolean noZeroValuesPresent = leveledGraph.values().stream()
                .noneMatch(i -> i == 0);
        assertTrue(noZeroValuesPresent);
    }

    @Test
    public void findTopLevelForGraph() throws Exception {
        Map<Task, Integer> leveledGraph = GraphProperties.findTopLevel(graph, 1);
        // Sanity Checks
        leveledGraph.forEach((task, integer) -> {
            assertNotNull(integer);
            assertNotNull(task);
        });

        boolean someZeroValuesPresent = leveledGraph.values().stream()
                .anyMatch(i -> i == 0);
        assertTrue(someZeroValuesPresent);
    }

    @Test
    public void lengthOfLongestPath() {
        Integer criticalPathLength = GraphProperties.longestLengthOfCriticalPath(graph);
        assertTrue(criticalPathLength != null);
    }

    @Test
    public void identifyCriticalPaths() {
        List<List<Task>> result = GraphProperties.findCriticalPath(graph);
        result.forEach(path -> {
            assertFalse(path.size() == 0);
        });
    }
}
