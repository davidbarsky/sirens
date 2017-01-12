package com.davidbarsky.dag;

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.davidbarsky.dag.models.Task;
import org.junit.Test;


public class TopologicalSorterTest {
    @Test
    public void invoke() {
        TopologicalSorter sorter = new TopologicalSorter();
        List<Task> sortResult = TopologicalSorter.generateGraph(20);

        assertEquals(20, sortResult.size());
    }
}
