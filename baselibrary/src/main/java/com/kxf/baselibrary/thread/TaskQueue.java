package com.kxf.baselibrary.thread;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class TaskQueue {
    /** 是否是主线程队列*/
    final private boolean mIsMain;

    /** 最大异步任务数，默认值 3 */
    private int mMaxAsyncTaskCount = 3;

    /** 线程池大小（仅对背景队列有效），默认值 2*/
    private int mMaximumPoolSize = 2;

    private ArrayDeque<TaskWrap> mQueue = new ArrayDeque<>(100);
    private HashMap<String,TaskWrap> mTagMap = new HashMap<>(50);
    private HashMap<String,TaskWrap> mExecutingMap = new HashMap<>();

    private ThreadPoolExecutor mThreadPool;
    private GlobalThreadQueue mThreadQueue;

    private OnTaskQueueListener mListener;

    /**
     * 创建一个主线程工作队列
     * @return TaskQueue
     */
    public static TaskQueue createMainQueue(){
        return new TaskQueue(true);
    }

    /**
     * 创建一个背景线程工作队列
     * @return TaskQueue
     */
    public static TaskQueue createBackgroundQueue(){
        return new TaskQueue(false);
    }

    /**
     * 创建一个背景线程工作队列
     * @return TaskQueue
     */
    public static TaskQueue createBackgroundQueue(GlobalThreadQueue threadQueue){
        return new TaskQueue(threadQueue);
    }

    protected TaskQueue(boolean isMain){
        mIsMain = isMain;

        if (!mIsMain){
            mThreadPool = ThreadPoolExecutorUtil.newThreadPoolExecutor( 1,
                    mMaximumPoolSize,
                    5000,
                    TimeUnit.MILLISECONDS);
        }
    }

    protected TaskQueue(GlobalThreadQueue threadQueue){
        mIsMain = false;
        mThreadQueue = threadQueue;
    }

    final public boolean isMainQueue() {
        return mIsMain;
    }

    /**
     * 获取当前正在执行的任务的数量
     * @return 任务数
     */
    public int getExcutingTaskCount() {
        return mExecutingMap.size();
    }

    public int getMaximumPoolSize() {
        if (mThreadPool == null){
            return 0;
        }
        else{
            return mMaximumPoolSize;
        }
    }

    /**
     * 设置线程池最大值，默认 2。
     * 线程池大小仅对背景任务队列并且是使用 ThreadPoolExecutor 做为线程执行器时有效。
     * 如果 TaskQueue 使用的是 GlobalThreadQueue 做为线程执行器则该方法无效果。
     * @param maximumPoolSize 线程池最大值
     */
    public void setMaximumPoolSize(int maximumPoolSize) {
        if (maximumPoolSize < 1){
            maximumPoolSize = 0;
        }

        this.mMaximumPoolSize = maximumPoolSize;

        if (mThreadPool != null){
            mThreadPool.setMaximumPoolSize(mMaximumPoolSize);
        }
    }

    public int getMaxAsyncTaskCount() {
        return mMaxAsyncTaskCount;
    }

    /**
     * 任务最大并发数，默认值 3。
     * 实际最大并发任务数还取决于线程池的 MaximumPoolSize（{@link ThreadPoolExecutor#setMaximumPoolSize}） 大小，
     * 实际的并发数不会大于 MaximumPoolSize 指定的值。
     *
     * @param taskCount 并发数
     */
    public void setMaxAsyncTaskCount(int taskCount) {
        if (taskCount < 1){
            taskCount = 0;
        }

        this.mMaxAsyncTaskCount = taskCount;
    }

    /**
     * 添加一个任务到队列中，如果队列为空则立即执行这个任务。
     * Task 使用 Tag 做为唯一标识，如果 Task 的 tag 为空或空串则会自动生成一个。
     * 如果队列中已有相同 Tag 的 Task，则会替换掉那个 Task。
     * 如果相同 Tag 值的任务正在执行中，则添加失败，返回 NO。
     * @param task 任务
     * @return 如果添加成功则返回 true，否则返回 false。
     */
    public boolean addTask(@NonNull Task task){
        synchronized (this){
            String tag = task.getTag();

            if (TextUtils.isEmpty(tag)){
                tag = UUID.randomUUID().toString();
            }

            task.mTag = tag;

            if (mExecutingMap.containsKey(tag)){
                return false;
            }

            task.mQueue = new WeakReference<>(this);

            TaskWrap wrap = mTagMap.get(tag);

            if (wrap != null){
                wrap.mTask = task;
            }
            else{
                wrap = new TaskWrap(task);
                mTagMap.put(tag,wrap);
                mQueue.addLast(wrap);
            }
        }

        execute();

        return true;
    }

    public List<Task> getAllTask(){
        List<Task> tasks = new ArrayList<>();
        synchronized (this){
            if(mQueue == null || mQueue.size() <= 0){
                return tasks;
            }

            Task task;
            for (TaskWrap taskWrap : mQueue) {
                task = taskWrap.mTask;
                if(task != null){
                    tasks.add(task);
                }
            }
            return tasks;
        }
    }

    /**
     * 清除所有等待执行的任务
     */
    public void clear(){
        synchronized(this){
            mQueue.clear();
            mTagMap.clear();
        }
    }

    /**
     * 从任务队列中移除任务，如果指定任务已经开始执行则该方法没有效果。
     * @param tag 任务标识
     */
    public void remove(@NonNull String tag){
        synchronized(this){
            TaskWrap wrap = mTagMap.remove(tag);
            if (wrap != null){
                mQueue.remove(wrap);
            }
        }
    }

    /**
     * 从任务队列中移除任务，如果指定任务已经开始执行则该方法没有效果。
     * @param task 任务
     */
    public void remove(@NonNull Task task){
        if (TextUtils.isEmpty(task.mTag)){
            return;
        }

        remove(task.mTag);
    }

    /**
     * 完成一个任务 <br>
     * 如果 tag 所指示的 {@link Task} 未添加到队列或未执行时调用则无效果  <br>
     * 如果在 tag 所指示的 {@link Task} 的 {@link Task#perform()} 方法内调用，则无论 {@link Task#perform()} 方法返回值是什么都会完成这个任务。 <br>
     * 如果在 tag 所指示的 {@link Task} 的 {@link Task#perform()} 方法执行完成并且返回 NO，之后调用则会结束这个任务。 <br>
     * 如果在 tag 所指示的 {@link Task} 的 {@link Task#perform()} 方法执行完成并且返回 YES，之后调用则无效果。 <br>
     *
     * @param tag 任务的标识
     */
    public void finish(@NonNull String tag){
        TaskWrap wrap;
        synchronized (this){
            wrap = mExecutingMap.get(tag);
            mExecutingMap.remove(tag);
        }

        OnTaskQueueListener listener = mListener;
        if (listener != null && wrap != null){
            listener.onFinish(this,wrap.mTask);
        }

        execute();
    }

    /**
     * 完成一个任务
     * @see #finish(String)
     */
    public void finish(@NonNull Task task){
        if (TextUtils.isEmpty(task.mTag)){
            return;
        }

        finish(task.mTag);
    }

    /**
     * 设置任务队列侦听器
     * @param listener OnTaskQueueListener 实例或 null
     */
    public void setOnTaskQueueListener(OnTaskQueueListener listener){
        mListener = listener;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 内部方法
    ///////////////////////////////////////////////////////////////////////////
    private void execute(){
        TaskWrap wrap;
        synchronized (this){
            if (mQueue.size() == 0 ||  mExecutingMap.size() >= mMaxAsyncTaskCount){
                //超过最大并发数或等待队列为空
                return;
            }

            wrap = mQueue.pollFirst();
            mTagMap.remove(wrap.mTask.mTag);

            mExecutingMap.put(wrap.mTask.mTag,wrap);
        }

        if (mIsMain){
            GlobalThreadQueue.shareInstance().postToMain(wrap);
        }
        else if(mThreadPool != null){
            mThreadPool.execute(wrap);
        }
        else if(mThreadQueue != null){
            mThreadQueue.postToWork(wrap);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Warp
    ///////////////////////////////////////////////////////////////////////////

    private class TaskWrap implements Runnable {
        private Task mTask;
        private TaskWrap(Task task){
            mTask = task;
        }

        @Override
        public void run() {
            OnTaskQueueListener listener = mListener;
            if (listener != null){
                listener.onPerformBefore(TaskQueue.this,mTask);
            }

            if (mTask.perform()){
                //任务完成，从执行列表中移除任务
                synchronized (TaskQueue.this){
                    mExecutingMap.remove(mTask.mTag);
                }

                listener = mListener;
                if (listener != null){
                    listener.onFinish(TaskQueue.this,mTask);
                }
            }

            execute();
        }
    }

    public interface OnTaskQueueListener {
        /**
         * 开启扫行一个任务前触发此事件。此事件将在队列线程中执行，使用时注意线程安全。
         * @param queue   任务所在的队列
         * @param task    当前任务
         */
        void onPerformBefore(TaskQueue queue, Task task);

        /**
         * 当一个任务完成时触发此事件。此事件将在队列线程中执行，使用时注意线程安全。
         * @param queue   任务所在的队列
         * @param task    当前任务
         */
        void onFinish(TaskQueue queue, Task task);
    }
}
