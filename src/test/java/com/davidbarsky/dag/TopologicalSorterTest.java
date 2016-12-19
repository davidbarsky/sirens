package com.davidbarsky.dag;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.davidbarsky.dag.models.TaskQueue;


public class TopologicalSorterTest {
    @Test
    public void invoke() {
        TopologicalSorter sorter = new TopologicalSorter();
        List<TaskQueue> sortResult = sorter.invoke(DAGGenerator.getErdosGNMSources(20));

        assertEquals(20, sortResult.size());
    }
}
