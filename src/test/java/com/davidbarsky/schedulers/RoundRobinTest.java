package com.davidbarsky.schedulers;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import com.davidbarsky.dag.models.TaskQueue;

public class RoundRobinTest {
	@Test
	public void invoke() {
		ArrayList<TaskQueue> queues = RoundRobin.generateSchedule(20);
		assertEquals(20, queues.size());

	}
}
