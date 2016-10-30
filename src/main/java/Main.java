import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import graph.GraphGenerator;
import info.rmarcus.ggen4j.GGenException;
import info.rmarcus.ggen4j.graph.Vertex;
import models.Task;
import models.TaskQueue;
import util.Pair;

public class Main {
    public static void main(String... args) {
        GGenWrapper wrapper = new GGenWrapper();
        try {
            ArrayList<Vertex> sources = wrapper.getSources();
            CacheGenerator cacheGenerator = new CacheGenerator();
            HashMap<Integer, Task> cache = cacheGenerator.makeCache(sources);
            System.out.println(cache);
        } catch (GGenException e) {
            e.printStackTrace();
        }

        try {
            Pair<ArrayList<TaskQueue>, HashMap<Integer, Task>> pair = makeTaskQueue();

            ArrayList<TaskQueue> taskQueues = pair.firstValue;
            HashMap<Integer, Task> lookupTable = pair.secondValue;

            List<Task> schedule = makeSchedule(taskQueues, lookupTable);
            schedule.forEach(t -> System.out.println(t.ID));
        } catch (GGenException e) {
            System.out.println(e);
        }
    }

    private static Pair<ArrayList<TaskQueue>, HashMap<Integer, Task>> makeTaskQueue() throws GGenException {
         GraphGenerator graphGenerator = new GraphGenerator();
         return graphGenerator.makeTaskQueue();
    }

    private static List<Task> makeSchedule(ArrayList<TaskQueue> taskQueues, HashMap<Integer, Task> lookupTable) {
        Actualizer actualizer = new Actualizer(lookupTable);

        return actualizer.makeSchedule(taskQueues);
    }
}
