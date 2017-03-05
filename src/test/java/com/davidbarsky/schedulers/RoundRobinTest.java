package com.davidbarsky.schedulers;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import com.davidbarsky.dag.models.states.MachineType;
import org.junit.Test;

import com.davidbarsky.dag.models.TaskQueue;

public class RoundRobinTest {
	@Test
	public void invoke() {
		BoundedScheduler roundRobin = new RoundRobin();
		List<TaskQueue> queues = roundRobin.generateSchedule(20, MachineType.SMALL);
		assertEquals(20, queues.size());
	}
}
