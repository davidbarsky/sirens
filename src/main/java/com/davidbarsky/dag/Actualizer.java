package com.davidbarsky.dag;

import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.TaskQueue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Actualizer {
  private Actualizer() {}

  public static List<TaskQueue> actualize(Collection<TaskQueue> tqs) {
    tqs.forEach(TaskQueue::unbuildAll);

    while (tqs.stream().anyMatch(TaskQueue::hasUnbuiltTask)) {
      if (tqs.stream().noneMatch(TaskQueue::buildNextUnbuiltTask)) {
        throw new DAGException(
            "Could not build task! Check input graph for cycles, and make sure all dependencies are in a task queue.");
      }
    }

    // check invariant: all tasks should now be built
    if (tqs.stream().anyMatch(TaskQueue::hasUnbuiltTask)) {
      //			String violating = tqs.stream()
      //					.flatMap(tq -> tq.getTasks().stream())
      //					.filter(t -> !t.isBuilt())
      //					.map(t -> String.valueOf(t.getID()))
      //					.collect(Collectors.joining(","));

      System.out.println(
          tqs.stream().map(TaskQueue::toShortString).collect(Collectors.joining("|")));

      throw new DAGException(
          "Could not build task! Check input graph for cycles, and make sure all dependencies are in a task queue.");
    }

    return new ArrayList<>(tqs);
  }

  public static List<TaskQueue> invokeWithTopo(Collection<TaskQueue> tqs, int[] topo) {
    tqs.forEach(TaskQueue::unbuildAll);

    int numTasks = tqs.stream().mapToInt(tq -> tq.getTasks().size()).sum();

    // build a list of TaskQueues ordered by the topological order of their tasks. i.e. if
    // a taskqueue has task 1, 3, and 9, it should be in the list at positions 1, 3, and 9.
    int[] values = new int[numTasks];
    TaskQueue[] unorderedTQs = new TaskQueue[numTasks];
    TaskQueue[] orderedTQs = new TaskQueue[numTasks];

    int count = 0;
    for (TaskQueue tq : tqs) {
      for (Task t : tq.getTasks()) {
        values[count] = topo[t.getID()];
        unorderedTQs[count] = tq;
        count++;
      }
    }

    for (int i = 0; i < numTasks; i++) {
      orderedTQs[values[i]] = unorderedTQs[i];
    }

    // now build the tasks in order
    for (TaskQueue tq : orderedTQs) {
      if (!tq.buildNextUnbuiltTask()) {
        throw new DAGException(
            "Could not build task! Check input graph for cycles, and make sure all dependencies are in a task queue.");
      }
    }

    // check invariant: all tasks should now be built
    if (tqs.stream().anyMatch(TaskQueue::hasUnbuiltTask)) {
      //			String violating = tqs.stream()
      //					.flatMap(tq -> tq.getTasks().stream())
      //					.filter(t -> !t.isBuilt())
      //					.map(t -> String.valueOf(t.getID()))
      //					.collect(Collectors.joining(","));

      System.out.println(
          tqs.stream()
                  .map(TaskQueue::toShortString)
                  .collect(Collectors.joining("|")));

      throw new DAGException(
          "Could not build task! Check input graph for cycles, and make sure all dependencies are in a task queue.");
    }

    return new ArrayList<>(tqs);
  }
}
