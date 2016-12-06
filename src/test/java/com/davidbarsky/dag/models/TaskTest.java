package com.davidbarsky.dag.models;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.expectThrows;

import java.util.HashMap;

import com.davidbarsky.dag.DAGException;
import org.junit.jupiter.api.Test;

import com.davidbarsky.dag.models.states.MachineType;

class TaskTest {
    @Test
    void testAddDependency() {
        Task firstTask = new Task(1, new TaskQueue(MachineType.SMALL), new HashMap<>());
        Task secondTask = new Task(2, new TaskQueue(MachineType.SMALL), new HashMap<>());

        firstTask.addDependency(5, secondTask);

        assertAll("Task.addDependency()",
                () -> assertTrue(firstTask.getDependencies().containsKey(secondTask)),
                () -> assertTrue(secondTask.getDependents().containsKey(firstTask)),
                () -> assertEquals(firstTask.getDependencies().get(secondTask), new Integer(5)),
                () -> assertEquals(secondTask.getDependents().get(firstTask), new Integer(5))
        );
    }

    @Test
    void buildable() {

        assertAll("buildable",
                () -> {
                    Task firstTask = new Task(1, new TaskQueue(MachineType.SMALL), new HashMap<>());
                    assertTrue(firstTask.buildable());
                },
                () -> {
                    Task firstTask = new Task(1, new TaskQueue(MachineType.SMALL), new HashMap<>());
                    Task secondTask = new Task(2, new TaskQueue(MachineType.SMALL), new HashMap<>());
                    firstTask.addDependency(5, secondTask);

                    assertFalse(firstTask.buildable());
                }
        );
    }

    @Test
    void testHashCode() {
        Task task = new Task(1, new TaskQueue(MachineType.LARGE), new HashMap<>());
        assertEquals(task.hashCode(), Integer.hashCode(1));
    }

    @Test
    void compareToWithUnbuiltTasks() {
        Task firstTask = new Task(1, new TaskQueue(MachineType.LARGE), new HashMap<>());
        Task secondTask = new Task(2, new TaskQueue(MachineType.LARGE), new HashMap<>());

        Throwable exception = expectThrows(DAGException.class, () -> {
            firstTask.compareTo(secondTask);
        });

        assertEquals("Tasks have not been built; there is no logical way to compare them.", exception.getMessage());
    }

}
