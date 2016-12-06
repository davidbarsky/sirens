package edu.brandeis.dag.models;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;

import edu.brandeis.dag.models.states.MachineType;

public class TaskQueue {
	private Queue<Task> tasks;
	private MachineType machineType;

	public TaskQueue(MachineType machineType) {
		this.tasks = new LinkedList<>();
		this.machineType = machineType;
	}
	
	public TaskQueue(MachineType machineType, List<Task> tasks) {
		this(machineType);
		tasks.forEach(this::add);
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

	public Queue<Task> getTasks() {
		return tasks;
	}

	@Override
	public String toString() {
		return "TaskQueue machine type: " + machineType.toString() + "\n"
				+ tasks.stream().map(t -> t.toString())
				.collect(Collectors.joining("\n"));
	}
}
