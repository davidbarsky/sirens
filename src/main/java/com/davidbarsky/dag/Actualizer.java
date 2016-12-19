package com.davidbarsky.dag;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;

import com.davidbarsky.dag.models.TaskQueue;

public class Actualizer {
	private Actualizer() { }
	
	public static @NonNull List<@NonNull TaskQueue> invoke(@NonNull Collection<@NonNull TaskQueue> tqs) {
		Set<TaskQueue> unbuilt = new HashSet<>(tqs);
		
		// keep going while any of our tasks are not built
		while (!unbuilt.isEmpty()) {
			// each time, try to build the next task on each task queue.
			boolean anyMatch = false;
			Iterator<TaskQueue> i = unbuilt.iterator();
			while (i.hasNext()) {
				TaskQueue tq = i.next();
				
				if (tq.buildNextUnbuiltTask())
					anyMatch = true;
				
				if (!tq.hasUnbuiltTask())
					i.remove();
			}
			
			// if we couldn't build any tasks in the queue, we should be done!
			if (!anyMatch) {
				break;
			}
		}
		
		// check invariant: all tasks should now be built
		if (tqs.stream().anyMatch(tq -> tq.hasUnbuiltTask())){
//			String violating = tqs.stream()
//					.flatMap(tq -> tq.getTasks().stream())
//					.filter(t -> !t.isBuilt())
//					.map(t -> String.valueOf(t.getID()))
//					.collect(Collectors.joining(","));
		
			throw new DAGException("Could not build task! Check input graph for cycles, and make sure all dependencies are in a task queue.");
		}

		return new ArrayList<>(tqs);
	}
}
