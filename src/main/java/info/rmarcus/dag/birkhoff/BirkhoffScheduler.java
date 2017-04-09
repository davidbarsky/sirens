package info.rmarcus.dag.birkhoff;

import sirens.dag.CostAnalyzer;
import sirens.dag.DAGGenerator;
import sirens.dag.models.Task;
import sirens.dag.models.TaskQueue;
import info.rmarcus.birkhoffvonneumann.CoeffAndMatrix;
import info.rmarcus.birkhoffvonneumann.learners.generalized_loss.MHJointPermutationLearner;
import info.rmarcus.dag.cca.CCAScheduler;
import info.rmarcus.dag.permsolve.PermutationSolver;
import info.rmarcus.ggen4j.graph.Vertex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

		CCAScheduler cca = new CCAScheduler(tasks);
		Collection<TaskQueue> tqs = cca.schedule(1000);
		System.out.println(tqs);
		System.out.println("Done!");
		
//		jps = new MHJointPermutationLearner(new int[] { tasks.size(), tasks.size() }, this::loss);
//		for (int i = 0; i < 10000; i++) {
//			if (i % 100 == 0)
//				System.out.println(i);
//			jps.iterate();
//		}

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
		bs.measure(5);
		//bs.measure(18);
		//System.out.println(bs.getBest());
		System.out.println(System.currentTimeMillis() - t);
	}
}
