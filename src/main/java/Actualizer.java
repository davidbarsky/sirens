import models.Task;
import models.TaskQueue;
import models.states.BuildStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class Actualizer {
    private HashMap<Integer, Task> lookupTable;

    public Actualizer(HashMap<Integer, Task> lookupTable) {
        this.lookupTable = lookupTable;
    }

    public ArrayList<Task> makeSchedule(ArrayList<TaskQueue> tasks) {
        ArrayList<Task> logicalSchedule = new ArrayList<>();
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
