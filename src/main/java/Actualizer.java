import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import models.Task;
import models.TaskQueue;
import models.states.BuildStatus;

public class Actualizer {
    private Map<Integer, Task> lookupTable;

    public Actualizer(Map<Integer, Task> lookupTable) {
        this.lookupTable = lookupTable;
    }

    public List<Task> makeSchedule(List<TaskQueue> tasks) {
        List<Task> logicalSchedule = new ArrayList<>();
        Optional<Task> previous = Optional.empty();

        for (TaskQueue taskQueue : tasks) {
            Optional<Task> task = this.getValidTask(taskQueue);
            if (task.isPresent()) {
                Task presentTask = task.get();
                presentTask.build(previous, 1);
                logicalSchedule.add(presentTask);
            } else {
                break;
            }
        }

        return logicalSchedule;
    }

    private Optional<Task> getValidTask(TaskQueue taskQueue) {
        Optional<Task> task = taskQueue.peek();
        if (task.isPresent() && isBuildable(task.get())) {
            return taskQueue.pop();
        } else {
            return Optional.empty();
        }
    }

    private Boolean isBuildable(Task task) {
        for (Integer dependency : task.dependencies) {
            if (lookupTable.get(dependency).buildStatus == BuildStatus.NotBuilt) {
                return false;
            }
        }

        return true;
    }
}
