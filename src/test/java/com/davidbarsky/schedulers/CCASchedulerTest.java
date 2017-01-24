package com.davidbarsky.schedulers;

import com.davidbarsky.dag.Actualizer;
import com.davidbarsky.dag.TopologicalSorter;
import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.TaskQueue;
import info.rmarcus.dag.cca.CCAScheduler;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

public class CCASchedulerTest {
    @Test
    public void schedule() throws Exception {
        List<Task> tasks = TopologicalSorter.generateGraph(40);
        CCAScheduler ccaScheduler = new CCAScheduler(tasks);

        Collection<TaskQueue> schedule = ccaScheduler.schedule(800);
        Actualizer.actualize(schedule).forEach(tq -> {
            assertNotNull(tq);
            boolean isBuilt = tq.getTasks().stream().allMatch(Task::isBuilt);
            assertTrue(isBuilt);
        });
    }

}
