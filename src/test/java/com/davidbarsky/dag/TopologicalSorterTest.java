package com.davidbarsky.dag;

import com.davidbarsky.dag.models.TaskQueue;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TopologicalSorterTest {
    @Test
    void invoke() {
        DAGGenerator generator = new DAGGenerator();
        TopologicalSorter sorter = new TopologicalSorter();
        ArrayList<TaskQueue> sortResult = sorter.invoke(generator.getErdosGNMSources(20));

        assertAll("Topological Sorter",
                () -> assertEquals(20, sortResult.size())
        );
    }
}
