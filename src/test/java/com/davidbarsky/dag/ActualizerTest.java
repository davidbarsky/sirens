package com.davidbarsky.dag;

import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.TaskQueue;
import com.davidbarsky.schedulers.RoundRobin;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ActualizerTest {
    @Test
    void actualize() {
        ArrayList<TaskQueue> tqs = (ArrayList<TaskQueue>) RoundRobin.invoke(2);

        List<TaskQueue> tasks = Actualizer.invoke(tqs);
        List<TaskQueue> copied = new ArrayList<>(tasks);

        copied.forEach(t -> Collections.sort(t.getTasks()));

        assertAll("Actualizer",
                () -> assertEquals(copied, tasks),
                () -> assertTrue(tasks.stream().map(TaskQueue::getTasks).allMatch(t -> t.stream().allMatch(Task::isBuilt))),
                () -> assertTrue(tasks.stream().map(TaskQueue::getTasks).allMatch(t -> t.stream().allMatch(Task::buildable)))
        );
    }
}
