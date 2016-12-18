package com.davidbarsky.dag.models;

import java.util.ArrayList;
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
		this.nextUnbuilt = 0;
	}

	public int geEndTimeOfLastBuiltTask() {
		if (nextUnbuilt == 0)
			return 0;
		
		return tasks.get(nextUnbuilt-1).getStartEndTime().get().getEnd();
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
		return nextUnbuilt < tasks.size();
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
