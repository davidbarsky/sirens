package com.davidbarsky.dag.models;

import com.davidbarsky.dag.DAGException;
import com.davidbarsky.dag.models.states.MachineType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
		if (this.hasTask(task)) {
			throw new DAGException("cannot add the same task to a task queue twice!");
		}

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
				.filter(Task::isBuilt)
				.mapToInt(t -> t.getStartEndTime().get().getStart())
				.min()
				.orElseThrow(() -> new RuntimeException("No tasks, cannot get start time!"));
	}

	public int getEndTime() {
		Optional<StartEndTime> startEndTime =
				tasks.get(tasks.size() - 1).getStartEndTime();

		if (!startEndTime.isPresent()) {
			Task unbuilt = tasks.get(tasks.size() -1);
			throw new RuntimeException("Task " + unbuilt + " is not built.");
		}

		return startEndTime.get().getEnd();
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

		nextUnbuilt = 0;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public boolean hasTask(Task task) {
		// TODO we could use a hashmap to make this faster
		return tasks.stream().mapToInt(Task::getID)
				.anyMatch(i -> i == task.getID());
	}

	@Override
	public String toString() {
		return  machineType.toString()
				+ tasks.stream().map(Task::toString)
				.collect(Collectors.joining(","));
	}

	public String toShortString() {
		return tasks.stream().map(t -> String.valueOf(t.getID())).collect(Collectors.joining(" "));
	}

	public void sortTasksByID() {
		tasks.sort((a, b) -> (a.getID() - b.getID()));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof  TaskQueue)) return false;

		TaskQueue taskQueue = (TaskQueue) o;

		return tasks.equals(taskQueue.tasks) && machineType == taskQueue.machineType;
	}

	@Override
	public int hashCode() {
		int result = tasks.hashCode();
		result = 31 * result + machineType.hashCode();
		return result;
	}
}
