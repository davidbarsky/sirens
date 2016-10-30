import info.rmarcus.ggen4j.GGenException;
import info.rmarcus.ggen4j.StaticGraphGenerator;
import info.rmarcus.ggen4j.graph.GGenGraph;
import info.rmarcus.ggen4j.graph.Vertex;

import java.util.ArrayList;

public class GGenWrapper {
    public ArrayList<Vertex> getSources() throws GGenException {
        StaticGraphGenerator graphGenerator = new StaticGraphGenerator();
        GGenGraph graph = graphGenerator.poisson2D(20, 5).generateGraph();

        return (ArrayList<Vertex>) graph.getSources();
    }
}
