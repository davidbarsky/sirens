package quick.sirens.schedulers;

import sirens.dag.Actualizer;
import sirens.dag.CostAnalyzer;
import sirens.dag.TopologicalSorter;
import sirens.models.Task;
import sirens.models.TaskQueue;
import sirens.models.states.MachineType;
import info.rmarcus.ggen4j.GGen;
import info.rmarcus.ggen4j.graph.GGenGraph;
import org.junit.Test;
import sirens.schedulers.EdgeZero;

import java.util.List;

import static org.junit.Assert.*;

public class EdgeZeroTest {

    private void verifyGraph(List<TaskQueue> builtGraph) throws Exception {
        CostAnalyzer.findCostOfBuiltTasks(builtGraph);
        builtGraph.forEach(tq -> {
            assertNotNull(tq.getStartTime());
            assertNotNull(tq.getEndTime());
            assertTrue(0 != tq.geEndTimeOfLastBuiltTask());
            tq.getTasks().forEach(t -> {
                assertTrue(t.isBuilt());
            });
        });
    }

    private List<TaskQueue> build(GGenGraph graph) {
        EdgeZero edgeZero = new EdgeZero();

        System.out.println(graph.toGraphviz());

        List<Task> taskGraph = TopologicalSorter.mapToTaskList(graph.allVertices());
        List<TaskQueue> schedule = edgeZero.generateSchedule(taskGraph, MachineType.SMALL);

        List<TaskQueue> builtGraph = Actualizer.actualize(schedule);
        return builtGraph;
    }

    @Test
    public void generateScheduleWithErdos() throws Exception {
        GGenGraph gg = GGen.generateGraph().erdosGNM(70, 100)
                .vertexProperty("latency").uniform(10, 30)
                .edgeProperty("networking").uniform(50, 120)
                .generateGraph().topoSort();

        List<TaskQueue> builtGraph = build(gg);
        verifyGraph(builtGraph);
    }

    @Test
    public void generateScheduleWithSparceLU() throws Exception {
        GGenGraph gg = GGen.dataflowGraph().sparseLU(10)
                .vertexProperty("latency").uniform(10, 30)
                .edgeProperty("networking").uniform(50, 120)
                .generateGraph().topoSort();

        List<TaskQueue> builtGraph = build(gg);
        verifyGraph(builtGraph);
    }

    @Test
    public void generateScheduleWithCholskey() throws Exception {
        GGenGraph gg = GGen.dataflowGraph().cholesky(16)
                .vertexProperty("latency").uniform(10, 30)
                .edgeProperty("networking").uniform(50, 120)
                .generateGraph().topoSort();

        List<TaskQueue> builtGraph = build(gg);
        verifyGraph(builtGraph);
    }

    @Test
    public void generateScheduleWithDenseLU() throws Exception {
        GGenGraph gg = GGen.dataflowGraph().denseLU(10)
                .vertexProperty("latency").uniform(10, 30)
                .edgeProperty("networking").uniform(50, 120)
                .generateGraph().topoSort();

        List<TaskQueue> builtGraph = build(gg);
        verifyGraph(builtGraph);
    }

    @Test
    public void generateScheduleWithForkJoin() throws Exception {
        GGenGraph gg = GGen.staticGraph().forkJoin(10, 15)
                .vertexProperty("latency").uniform(10, 60)
                .edgeProperty("networking").uniform(10, 60)
                .generateGraph().topoSort();

        List<TaskQueue> builtGraph = build(gg);
        verifyGraph(builtGraph);
    }

    @Test
    public void generateScheduleWithPoisson2D() throws Exception {
        GGenGraph gg = GGen.dataflowGraph().poisson2D(20, 6)
                .vertexProperty("latency").uniform(10, 60)
                .edgeProperty("networking").uniform(10, 60)
                .generateGraph().topoSort();

        List<TaskQueue> builtGraph = build(gg);
        verifyGraph(builtGraph);
    }
}
