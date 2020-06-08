package com.dodge.starter.dispatcher;


import com.dodge.starter.graph.MapDigraph;
import com.dodge.starter.task.ITask;

import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

/**
 * 任务启动器
 */
public class TaskStarter {


    private TaskContext taskContext;


    public TaskStarter() {
        taskContext = new TaskContext();
    }


    public void start(MapDigraph<ITask> mapDigraph) {
        initTask(mapDigraph);
        try {
            executeSyncTask(taskContext);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化任务序列，根据任务依赖关系，构建有向无环图
     *
     * @param mapDigraph 图
     */
    private void initTask(MapDigraph<ITask> mapDigraph) {
        // 根据依赖关系，添加边 Edge
        Set<ITask> taskSet = mapDigraph.nodeSet();
        for (ITask to : taskSet) {
            Set<ITask> depends = to.dependSet();
            for (ITask from : depends) {
                mapDigraph.addEdge(from, to);
            }
        }
        taskContext.buildTasks(mapDigraph);
        taskContext.taskDispatcher().start(taskContext.getTaskQueue()); // 分发任务
    }

    private void executeSyncTask(TaskContext environment) throws InterruptedException {
        BlockingQueue<ITask> queue = environment.taskDispatcher().getBlockingQueue();
        ITask task = queue.take(); // 从阻塞队列提取任务
        while (!task.isFinish()) {
            task.run();
            Set<ITask> tasks = environment.getDependOnTaskSet(task);
            for (ITask item : tasks) {
                CountDownLatch latch = environment.getTaskLatch(item);
                if (latch != null) {
                    latch.countDown();
                }
            }
            task = queue.take(); // 提取下一个任务
        }
    }


    public void cancel() {
        // TODO Dodge cancel task list

    }


    public void release() {
        taskContext.release();
    }


}
