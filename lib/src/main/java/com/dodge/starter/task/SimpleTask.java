package com.dodge.starter.task;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 任务实现
 */
public class SimpleTask implements ITask {

    public static ITask FINISH_TASK = new SimpleTask("End Task", ITask.FLAG_FINISH, null);


    private String name;
    private int flag = FLAG_DEFAULT;
    private Runnable runnable;
    private Set<ITask> dependSet = new HashSet<>();


    public SimpleTask(Runnable runnable) {
        this.runnable = runnable;
    }

    public SimpleTask(@ITask.Flag int flag, Runnable runnable) {
        this.flag = flag;
        this.runnable = runnable;
    }

    public SimpleTask(String name, @ITask.Flag int flag, Runnable runnable) {
        this.name = name;
        this.flag = flag;
        this.runnable = runnable;
    }

    @Override
    public boolean isMain() {
        return (flag & FLAG_MASK) != FLAG_ASYNC;
    }

    @Override
    public boolean isFinish() {
        return (flag & FLAG_MASK) == FLAG_FINISH;
    }


    @Override
    public String name() {
        return name;
    }

    @Override
    public void setFlag(int flag) {
        this.flag = flag;
    }

    @Override
    public void depend(ITask... task) {
        dependSet.clear();
        if (task != null) {
            dependSet.addAll(Arrays.asList(task));
        }
    }

    @Override
    public Set<ITask> dependSet() {
        return dependSet;
    }

    @Override
    public void run() {
        if (runnable != null) {
            runnable.run();
        }
    }

}
