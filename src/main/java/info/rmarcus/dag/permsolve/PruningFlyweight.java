package info.rmarcus.dag.permsolve;

import org.eclipse.jdt.annotation.Nullable;

import info.rmarcus.NullUtils;

public class PruningFlyweight {
	private int bestObservedCost = Integer.MAX_VALUE;
	private @Nullable ScheduleNode bestPartitions = null;
	
	public int getBestObservedCost() {
		return bestObservedCost;
	}
	
	public void setBestObservedCost(int n) {
		this.bestObservedCost = n;
	}
	
	public boolean isAboveBestObserved(int n) {
		return n > bestObservedCost;
	}
	
	public void recordNewObserved(int n, ScheduleNode sn) {
		if (n < bestObservedCost) {
			this.bestObservedCost = n;
			this.bestPartitions = sn;
		}		
	}
	
	public ScheduleNode getBestPartitions() {
		final ScheduleNode toR = bestPartitions;
		return NullUtils.orThrow(toR, 
				() -> new PermSolveException("No best schedule yet!"));
	}
}
