package edu.brandeis.dag;

import edu.brandeis.dag.models.StartEndTime;
import edu.brandeis.dag.models.Task;

import java.util.List;
import java.util.Optional;

public class Coster {
    private Coster() {}

    public static Integer findCost(List<Task> tasks) {
        return tasks.stream()
                .map(Task::getStartEndTime)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .mapToInt(StartEndTime::getDuration)
                .sum();
    }
}
