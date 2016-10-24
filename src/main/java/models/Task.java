package models;

import models.states.BuildStatus;

import java.util.ArrayList;
import java.util.Optional;

public class Task {
    public Integer ID;
    public BuildStatus buildStatus;
    public Optional<StartEndTime> startEndTime;
    public ArrayList<Integer> dependencies;

    public Task(Integer ID, BuildStatus buildStatus, Optional<StartEndTime> startEndTime, ArrayList<Integer> dependencies) {
        this.ID = ID;
        this.buildStatus = buildStatus;
        this.startEndTime = startEndTime;
        this.dependencies = dependencies;
    }

    public void build(Optional<Task> previous, Integer cost) {
        Integer startTime = 0;
        if (previous.isPresent()) {
            Optional<StartEndTime> prevTime = previous.get().startEndTime;
            if (prevTime.isPresent()) {
                startTime = prevTime.get().end;
            }
        }
        Integer endTime = startTime + cost;

        this.buildStatus = BuildStatus.Built;
        this.startEndTime = Optional.of(new StartEndTime(startTime, endTime));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        return ID.equals(task.ID);
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }
}
