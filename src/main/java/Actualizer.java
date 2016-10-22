import models.Task;
import models.TaskQueue;
import models.StartEndTime;
import models.states.BuildStatus;

import java.util.ArrayList;
import java.util.Optional;

public class Actualizer {
    public ArrayList<Task> makeSchedule(ArrayList<TaskQueue> tasks) {
        ArrayList<Task> logicalSchedule = new ArrayList<Task>();
        Optional<Task> previous = Optional.empty();

        for (TaskQueue queue : tasks) {
            Optional<Task> task = queue.take();
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
}
