package com.davidbarsky.experiments;

import com.davidbarsky.schedulers.EdgeZero;
import com.davidbarsky.schedulers.RoundRobin;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ExperimentRunnerTest {
    @Test
    public void runSeriesUnbounded() throws Exception {
        List<ExperimentResult> results = ExperimentRunner.runSeries(new EdgeZero(), 10, 20);
        results.forEach(Assert::assertNotNull);
        results.forEach(r -> {
            assertNotNull(r);
            assertEquals(r.schedulerName(), "class com.davidbarsky.schedulers.EdgeZero");
        });
    }

    @Test
    public void runSeriesBounded() throws Exception {
        List<ExperimentResult> results = ExperimentRunner.runSeries(new RoundRobin(), 10, 20);
        results.forEach(r -> {
            assertNotNull(r);
            assertEquals(r.schedulerName(), "class com.davidbarsky.schedulers.RoundRobin");
        });
    }

    @Test
    public void runExperimentUnbounded() throws Exception {
        ExperimentResult experimentResult = ExperimentRunner.runExperiment(new EdgeZero(), 30);
        assertEquals(experimentResult.numberOfNodes(), 30);
        assertEquals(experimentResult.schedulerName(), "class com.davidbarsky.schedulers.EdgeZero");
        assertNotNull(experimentResult.finalCost());
    }

    @Test
    public void runExperimentBounded() throws Exception {
        ExperimentResult experimentResult = ExperimentRunner.runExperiment(new RoundRobin(), 10);
        assertEquals(experimentResult.numberOfQueues(), 10);
        assertEquals(experimentResult.schedulerName(), "class com.davidbarsky.schedulers.RoundRobin");
        assertNotNull(experimentResult.finalCost());
    }
}
