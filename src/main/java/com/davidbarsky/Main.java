package com.davidbarsky;

import com.davidbarsky.dag.Actualizer;
import com.davidbarsky.dag.CostAnalyzer;
import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.TaskQueue;
import com.davidbarsky.dag.models.states.MachineType;
import com.davidbarsky.experiments.GraphGenerator;
import com.davidbarsky.schedulers.LinearCluster;

import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String... args) {
        List<Task> graph = GraphGenerator.genericGraph(4);
        LinearCluster linearCluster = new LinearCluster();

        List<TaskQueue> queues = linearCluster.generateSchedule(graph, MachineType.SMALL)
                .stream()
                .filter(tq -> !tq.getTasks().isEmpty())
                .collect(Collectors.toList());
        List<TaskQueue> builtTasks = Actualizer.actualize(queues);
        CostAnalyzer.findCostOfBuiltTasks(builtTasks);
    }
}
