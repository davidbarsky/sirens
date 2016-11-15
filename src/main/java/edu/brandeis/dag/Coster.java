package edu.brandeis.dag;

import edu.brandeis.dag.models.StartEndTime;
import edu.brandeis.dag.models.Task;
import edu.brandeis.dag.models.TaskQueue;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Coster {
    private Coster() {}

    public static Integer findCost(List<TaskQueue> tasks) {

        List<Task> allTasks =
                tasks.stream()
                        .map(TaskQueue::getTasks)
                        .flatMap(List::stream)
                        .collect(Collectors.toList());

        return allTasks.stream()
                .map(Task::getStartEndTime)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .mapToInt(StartEndTime::getDuration)
                .sum();
    }
}
