package edu.brandeis.dag;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;


import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import edu.brandeis.dag.models.Task;
import edu.brandeis.dag.models.TaskQueue;
import edu.brandeis.dag.models.states.MachineType;

class ActualizerTest {
	@Test
	public void actualize() {
		ArrayList<TaskQueue> tqs = (ArrayList<TaskQueue>) GraphGenerator.randomGraph(2);

		List<Task> tasks = Actualizer.actualize(tqs);
		List<Task> copied = new ArrayList<>(tasks);

		Collections.sort(copied);

		assertAll("Actualizer",
				() -> assertEquals(copied, tasks),
				() -> assertTrue(tasks.stream().allMatch(Task::isBuilt)),
				() -> assertTrue(tasks.stream().allMatch(Task::buildable))
				);
	}

	@Test
	public void impossibleActualize() {
		TaskQueue tq = new TaskQueue(MachineType.SMALL);

		Map<MachineType, Integer> latencies = new EnumMap<MachineType, Integer>(MachineType.class);
		latencies.put(MachineType.SMALL, 10);
		latencies.put(MachineType.LARGE, 10);


		Task t1 = new Task(1, tq, latencies);
		Task t2 = new Task(2, tq, latencies);

		t2.addDependency(10, t1);

		tq.add(t2);
		tq.add(t1);

		assertThrows(DAGException.class, () -> {
			Actualizer.actualize(Collections.singletonList(tq));
		});



	}
}
