package models;

import models.states.MachineType;

import java.util.LinkedList;
import java.util.Optional;

public class TaskQueue {
    public LinkedList<Task> tasks;
    public MachineType machineType;

    public TaskQueue(LinkedList<Task> tasks, MachineType machineType) {
        this.tasks = tasks;
        this.machineType = machineType;
    }

    public Optional<Task> take() {
        if (this.tasks.isEmpty()) {
            return Optional.empty();
        }

        Task task = this.tasks.peek();
        if (task.isReady()) {
            return Optional.of(this.tasks.pop());
        } else {
            return Optional.empty();
        }
    }
}
