package com.davidbarsky.schedulers;

import com.davidbarsky.dag.Actualizer;
import com.davidbarsky.dag.TopologicalSorter;
import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.TaskQueue;
import com.davidbarsky.dag.models.states.MachineType;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class EdgeZeroTest {

    EdgeZero edgeZero;
    List<TaskQueue> graph;

    @Before
    public void init() {
        edgeZero = new EdgeZero();
        graph = edgeZero.generateSchedule(30);
    }

    @Test
    public void generateSchedule() throws Exception {
        List<TaskQueue> schedule = Actualizer.actualize(graph);
        schedule.forEach(tq -> {
            assertNotNull(tq.getStartTime());
            assertNotNull(tq.getEndTime());
            assertTrue(0 != tq.geEndTimeOfLastBuiltTask());
            tq.getTasks().forEach(t -> {
                assertTrue(t.isBuilt());
            });
        });
    }
}
