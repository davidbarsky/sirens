package com.davidbarsky.dag.models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.davidbarsky.dag.models.states.MachineType;

public class TaskQueue {
	private List<Task> tasks;
	private MachineType machineType;
	private int nextUnbuilt;

	public TaskQueue(MachineType machineType) {
		this.tasks = new ArrayList<>();
		this.machineType = machineType;
		this.nextUnbuilt = 0;
	}

	public TaskQueue(MachineType machineType, List<Task> tasks) {
		this(machineType);
		this.tasks = new ArrayList<>(tasks);
		
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

	public boolean buildNextUnbuiltTask() {
		if (nextUnbuilt < tasks.size() && tasks.get(nextUnbuilt).build().isPresent()) {
			nextUnbuilt++;
			return true;
		}
		
		return false;
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
