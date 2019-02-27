package com.kxf.baselibrary.thread;

import java.lang.ref.WeakReference;


public abstract class Task {
    WeakReference<TaskQueue> mQueue;
    String mTag;

    /**
     * 任务标识，默认为 nil，如果该属性为 nil，则将此 Task 添加到队列时会自动生成一个
     * @return Tag
     */
    public String getTag(){
        return null;
    }

    /**
     * 执行任务
     * @return 如果任务完成了则返回 true。如果还没有完成(异步任务)返回 false，等完成后需要调用 finish 方法。
     */
    abstract public boolean perform();

    /**
     * 完成任务
     * 该方法如果在任务未添加到队列或未执行时调用则无效果
     * 该方法如果在 {@link #perform()} 方法内调用，则无论 {@link #perform()} 方法返回值是什么都会完成此任务。
     * 该方法如果在 {@link #perform()} 方法执行完成并且返回 false，之后调用则会结束这个任务。
     * 该方法如果在 {@link #perform()} 方法执行完成并且返回 true，之后调用则无效果。
     */
    final public void finish(){
        if (mQueue != null && mTag != null){
            TaskQueue queue = mQueue.get();
            if (queue != null)
                queue.finish(mTag);
        }
    }
}
