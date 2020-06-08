package com.dodge.demo;

import android.util.Log;

import com.dodge.starter.dispatcher.TaskStarter;
import com.dodge.starter.graph.MapDigraph;
import com.dodge.starter.task.ITask;
import com.dodge.starter.task.SimpleTask;


public class TaskAppTest {

    private TaskStarter taskStarter;

    public void start() {
        if (taskStarter == null) {
            taskStarter = new TaskStarter();
        }
        taskStarter.start(buildTasks());
    }

    private MapDigraph<ITask> buildTasks() {
        MapDigraph<ITask> mapDigraph = new MapDigraph<>();
        ITask task0 = createTask("Task 0", true, 100);
        ITask task1 = createTask("Task 1", false, 100);
        ITask task2 = createTask("Task 2", false, 300);
        ITask task3 = createTask("Task 3", false, 200);
        ITask task4 = createTask("Task 4", true, 100);
        ITask task5 = createTask("Task 5", true, 100);

        task1.depend(task0, task2, task3);
        task3.depend(task2);
        task4.depend(task2, task5);

        mapDigraph.add(task0);
        mapDigraph.add(task1);
        mapDigraph.add(task2);
        mapDigraph.add(task3);
        mapDigraph.add(task4);
        mapDigraph.add(task5);

        return mapDigraph;
    }

    private ITask createTask(String msg, boolean main, long time) {
        int flag = main ? ITask.FLAG_DEFAULT : ITask.FLAG_ASYNC;
        return new SimpleTask(msg, flag, () -> {
            if (!main) {
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.w("Dodge", "Thread name = " + Thread.currentThread().getName() + " start msg = " + msg);
        });
    }


}
