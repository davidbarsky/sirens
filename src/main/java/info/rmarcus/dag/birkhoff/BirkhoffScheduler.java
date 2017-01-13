package info.rmarcus.dag.birkhoff;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.davidbarsky.dag.Actualizer;
import com.davidbarsky.dag.CostAnalyzer;
import com.davidbarsky.dag.DAGGenerator;
import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.TaskQueue;

import info.rmarcus.birkhoffvonneumann.CoeffAndMatrix;
import info.rmarcus.birkhoffvonneumann.learners.generalized_loss.MHJointPermutationLearner;
import info.rmarcus.dag.permsolve.PermutationSolver;
import info.rmarcus.dynamic_critical_path.DynamicCriticalPath;
import info.rmarcus.ggen4j.graph.Vertex;

public class BirkhoffScheduler {

	private List<Task> tasks;
	private MHJointPermutationLearner jps;



	public void measure(int n) {
		Collection<Vertex> vertices = DAGGenerator.getSparseLU(n);
		tasks = DAGGenerator.verticesToTasks(vertices);
				
		System.out.println("Number of tasks: " + tasks.size());
		
//		Set<List<String>> clusters = DynamicCriticalPath.schedule(DAGGenerator.getVertexWeightMap(vertices), DAGGenerator.getEdgeWeightMap(vertices));
//		System.out.println(clusters);
//		Collection<TaskQueue> tqs = DAGGenerator.clustersToTasks(vertices, clusters);
//		tqs = Actualizer.actualize(tqs);
//		System.out.println("Cost: " + CostAnalyzer.getLatency((List<TaskQueue>) tqs));

		jps = new MHJointPermutationLearner(new int[] { tasks.size(), tasks.size() }, this::loss);
		for (int i = 0; i < 10000; i++) {
			if (i % 100 == 0)
				System.out.println(i);
			jps.iterate();
		}

	}

	public List<TaskQueue> getBest() {
		return permToTQs(jps.getBest().get(0), jps.getBest().get(1));
	}
	
	private List<TaskQueue> permToTQs(double[][] d, double[][] topo) {
		int[] perm = CoeffAndMatrix.asFlatPerm(d);
		int[] topoPerm = CoeffAndMatrix.asFlatPerm(topo);
		
		List<Task> t = new ArrayList<>(tasks.size());
		for (int i : perm) {
			t.add(tasks.get(i));
		}
		
		List<TaskQueue> tqs = PermutationSolver.topoSolve(t, topoPerm);
		return tqs;
	}
	
	private double loss(List<double[][]> d) {
		List<TaskQueue> tqs = permToTQs(d.get(0), d.get(1));
		int cost = CostAnalyzer.getLatency(tqs); 
		return cost;
	}

	public static void main(String[] args) {
		BirkhoffScheduler bs = new BirkhoffScheduler();
		long t = System.currentTimeMillis();
		bs.measure(12);
		//bs.measure(18);
		//System.out.println(bs.getBest());
		System.out.println(System.currentTimeMillis() - t);
	}
}
