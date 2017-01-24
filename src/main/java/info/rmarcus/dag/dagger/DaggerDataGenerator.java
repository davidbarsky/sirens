package info.rmarcus.dag.dagger;


import com.davidbarsky.dag.DAGGenerator;
import com.davidbarsky.dag.models.Task;
import com.davidbarsky.dag.models.states.MachineType;
import info.rmarcus.ggen4j.graph.Vertex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class DaggerDataGenerator {
    public static void writeDaggerData(Collection<Task> vertices, File toWriteTo) throws FileNotFoundException {
        try (PrintWriter pw = new PrintWriter(toWriteTo)) {
            // first, write out the number of vertices
            pw.println(vertices.size());

            List<Task> ordered = new ArrayList<>(vertices);
            Collections.sort(ordered, (a, b) -> a.getID() - b.getID());

            // for each vertex, write a comma-sep list of latencies for machine types
            for (Task v : ordered) {
                String s = Arrays.stream(MachineType.bySize())
                        .mapToInt(mt -> v.getLatencies().get(mt))
                        .mapToObj(i -> String.valueOf(i))
                        .collect(Collectors.joining(","));
                pw.println(s);
            }

            // now write out the comma-sep adj matrix. we will even write the redundant part
            // because what is this 1995?

            for (Task p : ordered) {
                String s = ordered.stream()
                        .map(c -> p.getDependents().getOrDefault(c, -1))
                        .map(i -> String.valueOf(i))
                        .collect(Collectors.joining(","));

                pw.println(s);
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        Collection<Task> v = DAGGenerator.verticesToTasks(DAGGenerator.getSparseLU(5));
        File f = new File("/home/ryan/projects/dagger/sparselu2.txt");

        writeDaggerData(v, f);
    }

}
