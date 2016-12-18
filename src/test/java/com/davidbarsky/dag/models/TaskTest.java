package com.davidbarsky.dag.models;



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Test;

import com.davidbarsky.dag.DAGException;
import com.davidbarsky.dag.models.states.MachineType;

class TaskTest {
	@Test
	void testAddDependency() {
		Task firstTask = new Task(1, new TaskQueue(MachineType.SMALL), new HashMap<>());
		Task secondTask = new Task(2, new TaskQueue(MachineType.SMALL), new HashMap<>());

		firstTask.addDependency(5, secondTask);

		assertTrue(firstTask.getDependencies().containsKey(secondTask));
		assertTrue(secondTask.getDependents().containsKey(firstTask));
		assertEquals(firstTask.getDependencies().get(secondTask), new Integer(5));
		assertEquals(secondTask.getDependents().get(firstTask), new Integer(5));

	}

	@Test
	void buildable1() {
		Task firstTask = new Task(1, new TaskQueue(MachineType.SMALL), new HashMap<>());
		assertTrue(firstTask.buildable());
	}

	@Test
	void buildable2() {
		Task firstTask = new Task(1, new TaskQueue(MachineType.SMALL), new HashMap<>());
		Task secondTask = new Task(2, new TaskQueue(MachineType.SMALL), new HashMap<>());
		firstTask.addDependency(5, secondTask);
		assertFalse(firstTask.buildable());
	}

	@Test
	void testHashCode() {
		Task task = new Task(1, new TaskQueue(MachineType.LARGE), new HashMap<>());
		assertEquals(task.hashCode(), Integer.hashCode(1));
	}

	@Test(expected=DAGException.class)
	void compareToWithUnbuiltTasks() {
		Task firstTask = new Task(1, new TaskQueue(MachineType.LARGE), new HashMap<>());
		Task secondTask = new Task(2, new TaskQueue(MachineType.LARGE), new HashMap<>());

		firstTask.compareTo(secondTask);
	}

}
