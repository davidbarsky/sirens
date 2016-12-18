package com.davidbarsky.dag;

import com.davidbarsky.schedulers.RoundRobin;
import info.rmarcus.ggen4j.graph.Vertex;
import org.junit.jupiter.api.Test;

import com.davidbarsky.dag.models.TaskQueue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DAGGeneratorTest {

    @Test
    void randomGraph() {
        ArrayList<TaskQueue> tqs = RoundRobin.invoke(4);
        assertAll("Task Queue",
                () -> assertEquals(4, tqs.size()),
                () -> assertNotEquals(5, tqs.size()),
                () -> assertNotNull(tqs)
        );
    }

    @Test
    void getErdosGMNSources() {
        DAGGenerator generator = new DAGGenerator();
        Collection<Vertex> sources = generator.getErdosGNMSources(20);
        assertAll("Erdos GMN Generator",
                () -> assertNotEquals(sources.size(), 0),
                () -> assertNotNull(sources),
                () -> assertEquals(20, sources.size())
        );

        sources.stream().forEach(vertex -> assertAll("Each individual vertex",
                () -> assertNotNull(vertex),
                () -> assertNotNull(vertex.getParents()),
                () -> assertNotNull(vertex.getChildren()),
                () -> assertEquals(vertex.hashCode(), Integer.hashCode(vertex.getID()))
        ));
    }

}
