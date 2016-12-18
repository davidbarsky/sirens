package com.davidbarsky.dag.models;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

import com.davidbarsky.dag.models.states.MachineType;

public class TaskQueue {
	private ArrayList<Task> tasks;
	private MachineType machineType;

	public TaskQueue(MachineType machineType) {
		this.tasks = new ArrayList<>();
		this.machineType = machineType;
	}

	public TaskQueue(MachineType machineType, ArrayList<Task> tasks) {
		this.tasks = tasks;
		this.machineType = machineType;
	}

	public void add(Task task) {
		this.tasks.add(task);
	}
	
	public int getLatestStartTime() {
		return tasks.stream().filter(t -> t.isBuilt())
				.max(Comparator.comparingInt(a -> a.getStartEndTime().get().getEnd()))
				.map(t -> t.getStartEndTime().get().getEnd())
				.orElse(0);
	}

	public MachineType getMachineType() {
		return machineType;
	}

	public boolean hasUnbuiltTask() {
		return tasks.stream().anyMatch(t -> !t.isBuilt());
	}

	public Optional<StartEndTime> buildNextUnbuiltTask() {
		return tasks.stream().filter(t -> !t.isBuilt())
				.findFirst()
				.map(t -> t.build())
				.orElse(Optional.empty());
	}

	public ArrayList<Task> getTasks() {
		return tasks;
	}

	@Override
	public String toString() {
		return "TaskQueue machine type: " + machineType.toString() + "\n"
				+ tasks.stream().map(t -> t.toString())
				.collect(Collectors.joining("\n"));
	}
}
