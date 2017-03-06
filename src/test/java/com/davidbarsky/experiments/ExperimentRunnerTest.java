package com.davidbarsky.experiments;

import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.states.MachineType;
import com.davidbarsky.schedulers.EdgeZero;
import com.davidbarsky.schedulers.RoundRobin;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ExperimentRunnerTest {
    @Test
    public void runSeriesUnbounded() throws Exception {
        List<ExperimentResult> results = ExperimentRunner.runSeries(new EdgeZero(), 10, 20, MachineType.SMALL);
        results.forEach(Assert::assertNotNull);
        results.forEach(r -> {
            assertNotNull(r);
            assertEquals(r.schedulerName(), "EdgeZero");
        });
    }

    @Test
    public void runSeriesBounded() throws Exception {
        List<ExperimentResult> results = ExperimentRunner.runSeries(new RoundRobin(), 10, 20, MachineType.SMALL);
        results.forEach(r -> {
            assertNotNull(r);
            assertEquals(r.schedulerName(), "RoundRobin");
        });
    }

    @Test
    public void runExperimentUnbounded() throws Exception {
        List<Task> genericGraph = GraphGenerator.genericGraph(50);
        ExperimentResult experimentResult = ExperimentRunner.runExperiment(new EdgeZero(), genericGraph.size(), genericGraph, MachineType.SMALL);
        assertEquals(experimentResult.numberOfNodes(), 50);
        assertEquals(experimentResult.schedulerName(), "EdgeZero");
        assertNotNull(experimentResult.finalCost());
    }

    @Test
    public void runExperimentBounded() throws Exception {
        List<Task> genericGraph = GraphGenerator.genericGraph(50);
        ExperimentResult experimentResult = ExperimentRunner.runExperiment(new RoundRobin(), 5, genericGraph, MachineType.SMALL);
        assertEquals(experimentResult.numberOfQueues(), 5);
        assertEquals(experimentResult.schedulerName(), "RoundRobin");
        assertNotNull(experimentResult.finalCost());
    }
}
