package info.rmarcus.dag.permsolve;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;

import info.rmarcus.NullUtils;

public class StarsAndBarsNode {
	private final int numItems;
	private final List<Integer> partitionPoints;
	private final Map<Integer, StarsAndBarsNode> children;

	public StarsAndBarsNode(int numItems) {
		List<Integer> l = new LinkedList<>();
		l.add(-1);
		partitionPoints = NullUtils.orThrow(Collections.unmodifiableList(l),
				() -> new PermSolveException("Collections::unmodifableList returned null!"));

		this.numItems = numItems;
		children = new HashMap<>();
	}

	private StarsAndBarsNode(List<Integer> previousPoints, int index, int numItems) {
		List<Integer> l = new LinkedList<>(previousPoints);
		l.add(index);
		partitionPoints = NullUtils.orThrow(Collections.unmodifiableList(l),
				() -> new PermSolveException("Collections::unmodifableList returned null!"));

		this.numItems = numItems;
		children = new HashMap<>();
	}

	private int getLastPartitionPoint() {
		if (partitionPoints.size() == 0)
			return 0;

		return NullUtils.orThrow(partitionPoints.get(partitionPoints.size()-1), 
				() -> new PermSolveException("Null entry in partition points!"));

	}

	public int getNumChildren() {
		return numItems - getLastPartitionPoint() - 2;
	}

	public StarsAndBarsNode getChild(int n) {
		if (n >= getNumChildren())
			throw new PermSolveException("Permutation node has no child #" + n);

		if (children.containsKey(n))
			return NullUtils.orThrow(children.get(n),
					() -> new PermSolveException("Children map entry became null after check"));

		// generate the child
		StarsAndBarsNode toR = new StarsAndBarsNode(partitionPoints,
				n + getLastPartitionPoint() + 1, 
				numItems);

		children.put(n, toR);

		return toR;

	}

	public List<List<Integer>> getPartitions() {
		Set<Integer> partitions = new HashSet<>(partitionPoints);
		List<List<Integer>> toR = new LinkedList<>();
		toR.add(new LinkedList<>());

		// collect everything in the current partition
		for (int i = 0; i < numItems; i++) {
			NullUtils.orThrow(toR.get(toR.size()-1),
					() -> new PermSolveException("Couldn't get a list from the end to add!"))
			.add(i);
			
			if (partitions.contains(i)) {
				// there's a split here!
				toR.add(new LinkedList<>());
			}
		}
		
		return toR;

	}

	public List<Pair> getAllDisconnectedPairs() {
		List<Pair> toR = new LinkedList<>();
		List<List<Integer>> partitions = getPartitions();
		
		for (int i = 0; i < partitions.size(); i++) {
			for (int j = i+1; j < partitions.size(); j++) {
				for (Integer item1 : NullUtils.orEmptyList(partitions.get(i))) {
					for (Integer item2 : NullUtils.orEmptyList(partitions.get(j))) {
						toR.add(new Pair(item1, item2));
					}
				}
			}
		}
		

		return toR;
	}
	
	/**
	 * Gets all the pairs of disconnected items that were created by the
	 * addition of the rightmost (highest value) seperator
	 * @return the disconnected pairs
	 */
	public List<Pair> getAdditionalDisconnectedPairsFromRightmostSeperator() {
		if (partitionPoints.size() <= 2)
			return getAllDisconnectedPairs();
		
		int midpoint = NullUtils.orThrow(partitionPoints.get(partitionPoints.size() - 1));
		int leftmost = NullUtils.orThrow(partitionPoints.get(partitionPoints.size() - 2));
		int rightmost = numItems;
		List<Pair> toR = new LinkedList<>();
		
		for (int i = leftmost; i <= midpoint; i++) {
			for (int j = midpoint+1; j < rightmost; j++) {
				toR.add(new Pair(i, j));
			}
		}
		
		return toR;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Set<Integer> s = new HashSet<>(partitionPoints);

		for (int i = 0; i < numItems; i++) {
			sb.append(i + " ");
			if (s.contains(i))
				sb.append("| ");
		}

		//sb.append(partitionPoints);

		@Nullable String toR = sb.toString();
		return (toR == null ? "none" : toR);
	}


	class Pair {
		int a;
		int b;
		
		private Pair(int a, int b) {
			this.a = a;
			this.b = b;
		}
		
		@Override
		public String toString() {
			return "<" + a + ", " + b + ">";
		}
	}
}
