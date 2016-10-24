package models;

import models.states.MachineType;

import java.util.LinkedList;
import java.util.Optional;

public class TaskQueue {
    private LinkedList<Task> tasks;
    public MachineType machineType;

    public TaskQueue(LinkedList<Task> tasks, MachineType machineType) {
        this.tasks = tasks;
        this.machineType = machineType;
    }

    public void add(Task task) {
        this.tasks.addLast(task);
    }

    public Optional<Task> peek() {
       if (this.tasks.isEmpty()) {
           return Optional.empty();
       }

       return Optional.of(tasks.peek());
    }

    public Optional<Task> pop() {
        if (this.tasks.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(this.tasks.pop());
    }
}
