package com.davidbarsky.dag;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.TaskQueue;
import com.davidbarsky.dag.models.states.MachineType;

public class ActualizerTest {


	@SuppressWarnings("null")
	public void impossibleActualize() {
		TaskQueue tq = new TaskQueue(MachineType.SMALL);

		Map<MachineType, Integer> latencies = new EnumMap<MachineType, Integer>(MachineType.class);
		latencies.put(MachineType.SMALL, 10);
		latencies.put(MachineType.LARGE, 10);

		Task t1 = new Task(0, tq, latencies);
		Task t2 = new Task(1, tq, latencies);

		t2.addDependency(10, t1);

		tq.add(t2);
		tq.add(t1);

		assertEquals(Actualizer.actualize(Collections.singletonList(tq)), null);
	}
}
