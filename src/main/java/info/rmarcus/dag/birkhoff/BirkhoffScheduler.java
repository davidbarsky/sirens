package info.rmarcus.dag.birkhoff;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.davidbarsky.dag.DAGGenerator;
import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.TaskQueue;

import info.rmarcus.dag.permsolve.PermutationSolver;

public class BirkhoffScheduler {
	public static void main(String[] args) {
		Collection<Task> tasks = DAGGenerator.verticesToTasks(DAGGenerator.getCholesky());
		
		List<Task> perm = new ArrayList<>(tasks);
		
		List<TaskQueue> tqs = PermutationSolver.solve(perm);		
		System.out.println(tqs);
	}
}
