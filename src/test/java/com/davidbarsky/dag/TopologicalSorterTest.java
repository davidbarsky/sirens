package com.davidbarsky.dag;

import com.davidbarsky.dag.models.TaskQueue;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;


public class TopologicalSorterTest {
    @Test
    public void invoke() {
        TopologicalSorter sorter = new TopologicalSorter();
        ArrayList<TaskQueue> sortResult = sorter.invoke(DAGGenerator.getErdosGNMSources(20));

        assertEquals(20, sortResult.size());
    }
}
