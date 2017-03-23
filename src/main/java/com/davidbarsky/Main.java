package com.davidbarsky;
import com.davidbarsky.dag.TopologicalSorter;
import com.davidbarsky.dag.models.Task;
import com.davidbarsky.experiments.Bench;
import info.rmarcus.ggen4j.GGen;
import info.rmarcus.ggen4j.graph.GGenGraph;

import java.util.List;

public class Main {
    public static void main(String... args) {
//        try {
//
//            GGenGraph gg = GGen.dataflowGraph().cholesky(6)
//                    .vertexProperty("latency").uniform(10, 30)
//                    .edgeProperty("networking").uniform(50, 120)
//                    .generateGraph().topoSort();
//            List<Task> tasks = TopologicalSorter.mapToTaskList(gg.allVertices());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        Bench.runExperiments();
    }
}
