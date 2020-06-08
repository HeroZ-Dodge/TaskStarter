package com.dodge.starter.dispatcher;

import android.util.Log;

import com.dodge.starter.task.ITask;
import com.dodge.starter.task.SimpleTask;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 任务调度器，根据任务队列，逐一提取并且分发给对应的任务处理器
 */
public class TaskDispatcher {

    private TaskContext taskContext;
    private BlockingQueue<ITask> blockingQueue; // 阻塞队列，为需要被阻塞的线程提供任务，用于同步执行
    private ExecutorService executorService;    // 单线程，提交任务序列

    public TaskDispatcher(TaskContext environment) {
        this.taskContext = environment;
        this.executorService = Executors.newSingleThreadExecutor();
        this.blockingQueue = new LinkedBlockingDeque<>();
    }


    public BlockingQueue<ITask> getBlockingQueue() {
        return blockingQueue;
    }


    public void start(List<ITask> taskQueue) {
        blockingQueue.clear();
        executorService.execute(() -> {
            for (ITask task : taskQueue) {
                Log.d("Dodge", "start: task = " + task.name());
                awaitTask(task);
                dispatchTask(task);
                Log.d("Dodge", "start: finish = " + task.name());
            }
            $dispatchSyncTask(SimpleTask.FINISH_TASK);
        });
    }

    /**
     * 分发任务
     *
     * @param task
     */
    private void dispatchTask(ITask task) {
        if (task.isMain()) {
            $dispatchSyncTask(task);
        } else {
            taskContext.asyncTaskHandler().execute(task);
        }
    }

    /**
     * 将任务提交至阻塞队列，同步执行
     *
     * @param task task
     */
    private void $dispatchSyncTask(ITask task) {
        try {
            blockingQueue.put(task);    // 往阻塞队列添加任务
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new IllegalStateException("");
        }
    }

    /**
     * 等待任务依赖
     *
     * @param task task
     */
    private void awaitTask(ITask task) {
        try {
            CountDownLatch countDownLatch = taskContext.getTaskLatch(task);
            if (countDownLatch != null) {
                countDownLatch.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new IllegalStateException("");
        }
    }


    private void release() {
        // TODO 释放资源
    }


}
