package com.davidbarsky.dag.models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.davidbarsky.dag.models.states.MachineType;

public class TaskQueue {
	private List<Task> tasks;
	private MachineType machineType;

	public TaskQueue(MachineType machineType) {
		this.tasks = new ArrayList<>();
		this.machineType = machineType;
	}

	public TaskQueue(MachineType machineType, List<Task> tasks) {
		this.tasks = tasks;
		this.machineType = machineType;
		
		tasks.forEach(this::add);
	}

	public void add(Task task) {
		task.unbuild();
		this.tasks.add(task);
		task.setTaskQueue(this);
	}

	public int getLatestStartTime() {
		return tasks.stream().filter(t -> t.isBuilt())
				.max(Comparator.comparingInt(a -> a.getStartEndTime().get().getEnd()))
				.map(t -> t.getStartEndTime().get().getEnd())
				.orElse(0);
	}
	
	public int getStartTime() {
		return tasks.stream()
				.filter(t -> t.isBuilt())
				.mapToInt(t -> t.getStartEndTime().get().getStart())
				.min()
				.orElseThrow(() -> new RuntimeException("No tasks, cannot get start time!"));
	}
	
	public int getEndTime() {
		return tasks.stream()
				.filter(t -> t.isBuilt())
				.mapToInt(t -> t.getStartEndTime().get().getEnd())
				.max()
				.orElseThrow(() -> new RuntimeException("No tasks, cannot get end time!"));
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

	public List<Task> getTasks() {
		return tasks;
	}

	@Override
	public String toString() {
		return "TaskQueue machine type: " + machineType.toString() + "\n"
				+ tasks.stream().map(t -> t.toString())
				.collect(Collectors.joining("\n")) + "\n";
	}
}
