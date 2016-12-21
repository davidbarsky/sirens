package com.davidbarsky.dag.models;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.davidbarsky.dag.models.states.MachineType;

public class TaskQueue {
	private ArrayList<Task> tasks;
	private MachineType machineType;
	private int nextUnbuilt;

	public TaskQueue(MachineType machineType) {
		this.tasks = new ArrayList<>();
		this.machineType = machineType;
		this.nextUnbuilt = 0;
	}

	public TaskQueue(MachineType machineType, List<Task> tasks) {
		this(machineType);
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
		return tasks.get(tasks.size()-1).getStartEndTime().get().getEnd();
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
	
	public void unbuildAll() {
		for (Task t : tasks) {
			t.unbuild();
			t.setTaskQueue(this);
		}
	}

	public List<Task> getTasks() {
		return tasks;
	}
	
	public boolean hasTask(int taskID) {
		// TODO we could use a hashmap to make this faster
		return tasks.stream().mapToInt(t -> t.getID())
				.anyMatch(i -> i == taskID);
				
		
	}

	@Override
	public String toString() {
		return  machineType.toString() + "\n"
				+ tasks.stream().map(t -> t.toString())
				.collect(Collectors.joining("\n")) + "\n";
	}
	
	public String toShortString() {
		return tasks.stream().map(t -> String.valueOf(t.getID())).collect(Collectors.joining(" "));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TaskQueue taskQueue = (TaskQueue) o;

		if (!tasks.equals(taskQueue.tasks)) return false;
		return machineType == taskQueue.machineType;
	}

	@Override
	public int hashCode() {
		int result = tasks.hashCode();
		result = 31 * result + machineType.hashCode();
		return result;
	}
}
