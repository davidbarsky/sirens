package models;

import models.states.BuildStatus;

import java.util.ArrayList;
import java.util.Optional;

public class Task {
    public String name;
    public BuildStatus buildStatus;
    public Optional<StartEndTime> startEndTime;
    public ArrayList<Task> dependencies;

    public Task(String name, BuildStatus buildStatus, Optional<StartEndTime> startEndTime, ArrayList<Task> dependencies) {
        this.name = name;
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

    public Boolean isReady() {
        for (Task dep : dependencies) {
            if (dep.buildStatus == BuildStatus.NotBuilt) {
                return false;
            }
        }

        return true;
    }
}
