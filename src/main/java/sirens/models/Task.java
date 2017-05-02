package sirens.models;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import sirens.dag.DAGException;
import sirens.models.states.BuildStatus;
import sirens.models.states.MachineType;

public class Task implements Comparable<Task> {
	private Integer id;

	private BuildStatus buildStatus;
	private Optional<StartEndTime> startEndTime;

	private Map<Task, Integer> dependencies;
	private Map<Task, Integer> dependents;

	private Map<MachineType, Integer> latencies;
	private TaskQueue tq;

	public Task(Integer id, TaskQueue tq, Map<MachineType, Integer> latencies) {
		this.id = id;
		this.buildStatus = BuildStatus.NOT_BUILT;
		this.startEndTime = Optional.empty();
		this.dependencies = new HashMap<>();
		this.dependents = new HashMap<>();
		this.latencies = latencies;
		this.tq = tq;
	}
	
	public Task(Integer id, Map<MachineType, Integer> latencies) {
		this.id = id;
		this.buildStatus = BuildStatus.NOT_BUILT;
		this.startEndTime = Optional.empty();
		this.dependencies = new HashMap<>();
		this.dependents = new HashMap<>();
		this.latencies = latencies;
	}

	public void addDependency(int networkCost, Task t) {
		if (t == null) {
			throw new NullPointerException("Null task given as a dependency!");
		}
		dependencies.put(t, networkCost);
		t.addDependent(networkCost, this);
	}

	private void addDependent(int networkCost, Task t) {
		dependents.put(t, networkCost);
	}

	public TaskQueue getTaskQueue() {
		return tq;
	}
	
	public void setTaskQueue(TaskQueue tq) {
		this.tq = tq;
	}

	public boolean isBuilt() {
		if (buildStatus == BuildStatus.BUILT && startEndTime.isPresent())
			return true;

		if (buildStatus == BuildStatus.BUILT && !startEndTime.isPresent())
			throw new DAGException("Invariant violated: should not be able to have a built task without a start and end time!");

		return false;
	}

	public boolean buildable() {
		return dependencies.keySet()
				.stream()
				.allMatch(t -> t.isBuilt());
	}

	public void unbuild() {
		this.buildStatus = BuildStatus.NOT_BUILT;
		this.startEndTime = Optional.empty();
	}
	
	private int findLatestEndingDep() {
		// find the latest ending dependency, or, if one of my deps
		// hasn't been built yet, fail.
		int maxEnd = Integer.MIN_VALUE;
		for (Task dep : this.dependencies.keySet()) {
			if (!dep.isBuilt())
				return -1;
			
			int theirEnd = dep.getStartEndTime().get().getEnd();
			maxEnd = (maxEnd < theirEnd ? theirEnd : maxEnd);
		}

		return maxEnd;
	}
	
	private int calculateNetworkingTime() {
		// figure out how much time we will need to spend doing networking...
		// (time we will use to write our data to other VMs with our dependents)
		int networkingTime = 0;
		for (Entry<Task, Integer> e : dependents.entrySet()) {
			networkingTime += (e.getKey().getTaskQueue() != this.getTaskQueue() ? e.getValue() : 0);
		}
		
		return networkingTime;
	}

	public boolean isLeaf() {
		return getDependents().isEmpty();
	}

	public boolean isSource() {
		return getDependencies().isEmpty();
	}

	public boolean isIndependent() {
		return getDependencies().isEmpty() && getDependents().isEmpty();
	}

	public int edgeWeight() {
		int inbound = getDependencies()
				.values()
				.stream()
				.mapToInt(Integer::intValue)
				.sum();

		int outbound = getDependents()
				.values()
				.stream()
				.mapToInt(Integer::intValue)
				.sum();

		return inbound + outbound;
	}

	public Optional<StartEndTime> build() {
		if (tq == null)
			return Optional.empty();
		
		if (isBuilt())
			return startEndTime;

		
		// find the latest ending dependency
		int latestDep = findLatestEndingDep();
		if (latestDep == -1)
			return Optional.empty();
		

		// find the latest starting task on my machine currently
		int latestStart = tq.geEndTimeOfLastBuiltTask();
		
		// compute how long we will have to spend on networking operations
		int networkingTime = calculateNetworkingTime();
		
		int myStart = Math.max(latestStart, latestDep);
		int myEnd = myStart + latencies.get(tq.getMachineType()) + networkingTime;

		this.startEndTime = Optional.of(new StartEndTime(myStart, myEnd, myEnd - networkingTime));
		this.buildStatus = BuildStatus.BUILT;

		return startEndTime;
	}

	public Map<Task, Integer> getDependencies() {
		return dependencies;
	}

	public Map<Task, Integer> getDependents() {
		return dependents;
	}

  public BuildStatus getBuildStatus() {
		return buildStatus;
	}

	public Optional<StartEndTime> getStartEndTime() {
		return startEndTime;
	}

	public Integer countDependents() {
		if (dependents.isEmpty()) return 0;
		return dependents.keySet().stream().mapToInt(Task::countDependents).sum();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Task))
			return false;

		return Objects.equals(this.id, ((Task) o).id);
	}

	@Override
	public int hashCode() {
		return Integer.hashCode(id);
	}
	
	@Override
	public String toString() {
		return id + (this.isBuilt() ? "B" : "");
	}

	@Override
	public int compareTo(Task that) {
	    if (!this.startEndTime.isPresent() || !that.startEndTime.isPresent())
	    	throw new DAGException("Tasks have not been built; there is no logical way to compare them.");

		if (this.startEndTime.get().getEnd() <= that.startEndTime.get().getStart() &&
				this.startEndTime.get().getStart() < this.startEndTime.get().getStart()) {
			return -1;
		} else if (this.startEndTime.get().getStart() >= that.startEndTime.get().getEnd() &&
				this.startEndTime.get().getEnd() >= that.startEndTime.get().getEnd()) {
			return 1;
		} else {
			return 0;
		}
	}
	
	public int getCostTo(Task task) {
		return dependents.getOrDefault(task, 0) + dependencies.getOrDefault(task, 0);
	}

	public int getID() {
		return id;
	}

	public Map<MachineType, Integer> getLatencies() {
		return latencies;
	}


}
