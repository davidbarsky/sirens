package edu.brandeis.dag.models;

import edu.brandeis.dag.DAGException;
import edu.brandeis.dag.models.states.MachineType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

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
