package com.davidbarsky.schedulers;

import com.davidbarsky.dag.Actualizer;
import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.TaskQueue;
import com.davidbarsky.dag.models.states.BuildStatus;
import org.junit.Test;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LinearClusterTest {
  @Test
  public void generateSchedule() throws Exception {
    UnboundedScheduler linearCluster = new LinearCluster();
    List<TaskQueue> schedule = linearCluster.generateSchedule(30);
    List<Task> sortedTasks =
        schedule
            .stream()
            .flatMap(taskQueue -> taskQueue.getTasks().stream())
            .sorted(Comparator.comparingLong(Task::getID))
            .collect(Collectors.toList());

    assertTrue(30 < sortedTasks.size());
  }

  @Test
  public void linearClusterCanBeActualized() throws Exception {
    UnboundedScheduler linearCluster = new LinearCluster();
    List<TaskQueue> schedule = linearCluster.generateSchedule(30);

    List<TaskQueue> actualizedSchedule = Actualizer.actualize(schedule);
    assertNotNull(actualizedSchedule);
    assertTrue(
        actualizedSchedule
            .stream()
            .flatMap(tq -> tq.getTasks().stream())
            .noneMatch(task -> task.getBuildStatus() == BuildStatus.NOT_BUILT));
  }
}
