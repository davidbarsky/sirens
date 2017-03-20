package com.davidbarsky.schedulers;

import com.davidbarsky.dag.Actualizer;
import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.TaskQueue;
import com.davidbarsky.dag.models.states.BuildStatus;
import com.davidbarsky.dag.models.states.MachineType;
import com.davidbarsky.experiments.GraphGenerator;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class LinearClusterTest {

    @Test
    public void generateSchedule() throws Exception {
        List<Task> genericGraph = GraphGenerator.genericGraph(50);
        List<TaskQueue> schedule = new LinearCluster().generateSchedule(genericGraph, MachineType.SMALL);

        List<TaskQueue> actualizedSchedule = Actualizer.actualize(schedule);

        List<Task> tasks = actualizedSchedule
                .stream()
                .flatMap(tq -> tq.getTasks().stream())
                .distinct()
                .collect(Collectors.toList());

        assertEquals(50, tasks.size());

        assertNotNull(actualizedSchedule);
        assertTrue(actualizedSchedule.stream()
                .flatMap(tq -> tq.getTasks().stream())
                .noneMatch(task -> task.getBuildStatus() == BuildStatus.NOT_BUILT));

        assertTrue(actualizedSchedule.stream()
                .flatMap(tq -> tq.getTasks().stream())
                .allMatch(task -> task.getBuildStatus() == BuildStatus.BUILT));
    }
}
