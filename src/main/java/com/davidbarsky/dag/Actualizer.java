package com.davidbarsky.dag;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.eclipse.jdt.annotation.NonNull;

import com.davidbarsky.dag.models.TaskQueue;

public class Actualizer {
	private Actualizer() { }
	
	public static @NonNull ArrayList<@NonNull TaskQueue> invoke(@NonNull Collection<@NonNull TaskQueue> tqs) {

		// keep going while any of our tasks are not built
		while (tqs.stream().anyMatch(TaskQueue::hasUnbuiltTask)) {
			// each time, try to build the next task on each task queue.
			boolean builtAny = tqs.stream()
					.map(TaskQueue::buildNextUnbuiltTask)
					.anyMatch(Optional::isPresent);
			
			// if we couldn't build any tasks in the queue, we should be done!
			if (!builtAny) {
				break;
			}
		}
		
		// check invariant: all tasks should now be built
		if (tqs.stream().anyMatch(tq -> tq.hasUnbuiltTask())){
			throw new DAGException("Could not build task! Check input graph for cycles, and make sure all dependencies are in a task queue.");
		}

		return new ArrayList<>(tqs);
	}
}
