package sirens;

import sirens.dag.Actualizer;
import sirens.dag.TopologicalSorter;
import sirens.dag.models.Task;
import sirens.dag.models.TaskQueue;
import info.rmarcus.dag.cca.CCAScheduler;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class CCASchedulerTest {
    @Test
    public void schedule() throws Exception {
        Collection<Task> tasks = TopologicalSorter.generateGraph(40);
        CCAScheduler ccaScheduler = new CCAScheduler(tasks);

        Collection<TaskQueue> schedule = ccaScheduler.schedule(800);
        Actualizer.actualize(schedule).forEach(tq -> {
            assertNotNull(tq);
            boolean isBuilt = tq.getTasks().stream().allMatch(Task::isBuilt);
            assertTrue(isBuilt);
        });
    }

}
