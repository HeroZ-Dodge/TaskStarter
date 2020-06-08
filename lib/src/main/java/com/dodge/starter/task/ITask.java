package com.dodge.starter.task;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Set;

public interface ITask extends Runnable {

    int FLAG_MASK = 0xFFFF;

    int FLAG_DEFAULT = 0x0000;        // 异步任务标识
    int FLAG_ASYNC = 0x0001;        // 异步任务标识
    int FLAG_FINISH = 0x002;       // 任务结束标识


    @IntDef({FLAG_DEFAULT, FLAG_ASYNC, FLAG_FINISH})
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    @Retention(RetentionPolicy.SOURCE)
    @interface Flag {
    }


    boolean isMain();

    boolean isFinish();

    String name();

    void setFlag(@Flag int flag);

    void depend(ITask... task);

    Set<ITask> dependSet();

}
