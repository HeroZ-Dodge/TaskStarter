package com.dodge.starter.dispatcher;

import android.util.Pair;

import com.dodge.starter.graph.MapDigraph;
import com.dodge.starter.task.ITask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * 任务上线文
 * 持有和任务执行相关的信息
 */
public class TaskContext {

    private boolean debug = false;
    private MapDigraph<ITask> mapDigraph;
    private List<ITask> taskQueue;
    private Map<ITask, CountDownLatch> latchMap = new HashMap<>(); //
    private TaskDispatcher taskDispatcher;
    private AsyncTaskExecutor asyncTaskExecutor;


    public TaskContext() {
        this.taskDispatcher = new TaskDispatcher(this);
        this.asyncTaskExecutor = new AsyncTaskExecutor(this);
    }


    public TaskDispatcher taskDispatcher() {
        return taskDispatcher;
    }

    public AsyncTaskExecutor asyncTaskHandler() {
        return asyncTaskExecutor;
    }


    public List<ITask> getTaskQueue() {
        return taskQueue;
    }

    public void buildTasks(MapDigraph<ITask> digraph) {
        this.mapDigraph = digraph;
        // 拓扑排序
        Pair<Boolean, List<ITask>> sortResult = digraph.topologicalSort();
        if (sortResult.first) {
            taskQueue = sortResult.second;
        } else {
            throw new IllegalStateException("任务循环依赖");
        }
        // 依赖关系
        latchMap.clear();
        for (ITask task : taskQueue) {
            Set<ITask> dependSet = getDependTaskSet(task);    // 依赖的任务
            CountDownLatch latch = dependSet.isEmpty() ? null : new CountDownLatch(dependSet.size());
            latchMap.put(task, latch);
        }
    }

    public CountDownLatch getTaskLatch(ITask task) {
        return latchMap.get(task);
    }


    /**
     * 获取依赖的task集合
     */
    public Set<ITask> getDependTaskSet(ITask task) {
        return mapDigraph.getInDegreeSet(task);
    }

    /**
     * 获取依赖于该task的集合
     */
    public Set<ITask> getDependOnTaskSet(ITask task) {
        return mapDigraph.getOutDegreeSet(task);
    }

    public void release() {
        mapDigraph = null;
        taskQueue = null;
        latchMap.clear();
    }


}
