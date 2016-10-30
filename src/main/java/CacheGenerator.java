import info.rmarcus.ggen4j.graph.Vertex;
import models.Task;
import models.states.BuildStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

public class CacheGenerator {
    public HashMap<Integer, Task> makeCache(ArrayList<Vertex> sources) {
       HashMap<Integer, Task> table = new HashMap<>();

       for (Vertex vertex : sources) {
           dfs(vertex, table);
           for (Vertex child : vertex.getChildren().keySet()) {
               if (!table.containsKey(child.getID())) {
                   dfs(vertex, table);
               }
           }
       }

       return table;
    }

    private void dfs(Vertex vertex, HashMap<Integer, Task> table) {
        ArrayList<Integer> dependencies = this.getDependencies(vertex.getParents().keySet());
        Task task = new Task(vertex.getID(), BuildStatus.NotBuilt, Optional.empty(), dependencies);

        table.put(task.ID, task);

        for (Vertex child : vertex.getChildren().keySet()) {
            if (!table.containsKey(child.getID())) {
                dfs(child, table);
            }
        }
    }

    private ArrayList<Integer> getDependencies(Set<Vertex> vertexSet) {
        ArrayList<Integer> dependencies = new ArrayList<>();
        for (Vertex vertex : vertexSet) {
            dependencies.add(vertex.getID());
        }

        return dependencies;
    }
}
