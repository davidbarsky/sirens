package quick.sirens.schedulers;

import static org.junit.Assert.assertEquals;

import java.util.List;

import sirens.experiments.GraphGenerator;
import sirens.models.Task;
import sirens.models.states.MachineType;
import org.junit.Test;

import sirens.models.TaskQueue;
import sirens.schedulers.BoundedScheduler;
import sirens.schedulers.RoundRobin;

public class RoundRobinTest {
	@Test
	public void invoke() {
		BoundedScheduler roundRobin = new RoundRobin();
		List<Task> graph = GraphGenerator.genericGraph(20);
		List<TaskQueue> queues = roundRobin.generateSchedule(graph, 4, MachineType.SMALL);
		assertEquals(4, queues.size());
	}
}
