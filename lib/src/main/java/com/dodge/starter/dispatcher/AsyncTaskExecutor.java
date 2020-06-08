package com.dodge.starter.dispatcher;

import android.util.Log;

import com.dodge.starter.task.ITask;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步任务处理器
 */
public class AsyncTaskExecutor {

    // 获得当前CPU的核心数
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    // 设置线程池的核心线程数2-4之间,但是取决于CPU核数
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));

    private TaskContext taskContext;
    private ExecutorService executorService;


    public AsyncTaskExecutor(TaskContext environment) {
        this.taskContext = environment;
        this.executorService = Executors.newFixedThreadPool(CORE_POOL_SIZE);
    }


    public void execute(ITask task) {
        executorService.execute(() -> {
            task.run();
            Set<ITask> tasks = taskContext.getDependOnTaskSet(task);
            Log.d("Dodge", "execute: 找到依赖 task = " + task.name() + " 的任务， size = " + tasks.size());
            for (ITask item : tasks) {
                CountDownLatch latch = taskContext.getTaskLatch(item);
                Log.d("Dodge", "execute: 找到依赖 task = " + task.name() + " 的任务， next task = " + item.name());
                if (latch != null) {
                    latch.countDown();
                }
            }
        });
    }


}
