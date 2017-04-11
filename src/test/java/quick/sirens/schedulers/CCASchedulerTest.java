package quick.sirens.schedulers;

import sirens.dag.Actualizer;
import sirens.experiments.GraphGenerator;
import sirens.models.Task;
import sirens.models.TaskQueue;
import info.rmarcus.dag.cca.CCAScheduler;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class CCASchedulerTest {
    @Test
    public void schedule() throws Exception {
        Collection<Task> tasks = GraphGenerator.genericGraph(20);
        CCAScheduler ccaScheduler = new CCAScheduler(tasks);

        Collection<TaskQueue> schedule = ccaScheduler.schedule(800);
        Actualizer.actualize(schedule).forEach(tq -> {
            assertNotNull(tq);
            boolean isBuilt = tq.getTasks().stream().allMatch(Task::isBuilt);
            assertTrue(isBuilt);
        });
    }

}
