package com.davidbarsky.dag;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNull;

import com.davidbarsky.dag.models.TaskQueue;

public class Actualizer {
	private Actualizer() { }
	
	public static @NonNull List<@NonNull TaskQueue> invoke(@NonNull Collection<@NonNull TaskQueue> tqs) {

		// keep going while any of our tasks are not built
		while (tqs.stream().anyMatch(TaskQueue::hasUnbuiltTask)) {
			// each time, try to build the next task on each task queue.
			boolean builtAny = tqs.stream()
					.map(TaskQueue::buildNextUnbuiltTask)
					.anyMatch(b -> b);
			
			// if we couldn't build any tasks in the queue, we should be done!
			if (!builtAny) {
				break;
			}
		}
		
		// check invariant: all tasks should now be built
		if (tqs.stream().anyMatch(tq -> tq.hasUnbuiltTask())){
			String violating = tqs.stream()
					.flatMap(tq -> tq.getTasks().stream())
					.filter(t -> !t.isBuilt())
					.map(t -> String.valueOf(t.getID()))
					.collect(Collectors.joining(","));
		
			throw new DAGException("Could not build task! Check input graph for cycles, and make sure all dependencies are in a task queue. Unbuilt tasks: " + violating);
		}

		return new ArrayList<>(tqs);
	}
}
