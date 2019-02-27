package com.kxf.baselibrary.thread;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class GlobalThreadQueue {
    protected ThreadPoolExecutor mThreadPool;
    protected Handler mHandler = new Handler(Looper.getMainLooper());

    protected GlobalThreadQueue(){
        this(3,10,5000);
    }

    /**
     * 创建线程队列
     * @param corePoolSize 线程池中所保存的线程数，包括空闲线程
     * @param maxPoolSize  线程池的最大线程数
     * @param keepAliveTime 空闲线程的存活时间（单位毫秒）
     */
    public GlobalThreadQueue (int corePoolSize,int maxPoolSize,int keepAliveTime){
        if (keepAliveTime < 100){
            keepAliveTime = 100;
        }

        mThreadPool = ThreadPoolExecutorUtil.newThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.MILLISECONDS);
    }

    private static GlobalThreadQueue singleton = null;
    public static GlobalThreadQueue shareInstance(){
        if (singleton == null){
            synchronized (GlobalThreadQueue.class){
                singleton = new GlobalThreadQueue();
            }
        }
        return singleton;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 属性
    ///////////////////////////////////////////////////////////////////////////
    public int getCorePoolSize() {
        return mThreadPool.getCorePoolSize();
    }

    public void setCorePoolSize(int corePoolSize) {
        mThreadPool.setCorePoolSize(corePoolSize);
    }

    public int getMaxPoolSize() {
        return mThreadPool.getMaximumPoolSize();
    }

    public void setMaxPoolSize(int maxPoolSize) {
        mThreadPool.setMaximumPoolSize(maxPoolSize);
    }

    public long getKeepAliveTime() {
        return mThreadPool.getKeepAliveTime(TimeUnit.MILLISECONDS);
    }

    public void setKeepAliveTime(long keepAliveTime) {
        mThreadPool.setKeepAliveTime(keepAliveTime, TimeUnit.MILLISECONDS);
    }

    protected ThreadPoolExecutor getThreadPoolExecutor(){
        return mThreadPool;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 线程方法
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 向主线程执行队列添加一个 runnable
     * @param runnable Runnable 实例。
     */
    public synchronized  void postToMain(Runnable runnable){
        mHandler.post(runnable);
    }

    /**
     * 向主线程执行队列添加一个 runnable
     * @param runnable Runnable 实例。
     * @param delayMillis 延时时间，单位毫秒。
     */
    public synchronized  void postToMain(Runnable runnable, long delayMillis){
        mHandler.postDelayed(runnable, delayMillis);
    }

    /**
     * 从主线程执行队列移除一个 runnable
     * @param runnable Runnable 实例。
     */
    public synchronized  void removeFromMain(Runnable runnable){
        mHandler.removeCallbacks(runnable);
    }

    /**
     * 将任务添加到式作线程队列
     * @param runnable Runnable 实例。
     */
    public synchronized  void postToWork(Runnable runnable){
        mThreadPool.execute(runnable);
    }

    /**
     * 从工作线程执行队列移除一个 runnable
     * @param runnable Runnable 实例。
     */
    public synchronized void removeFromWork(Runnable runnable) {
        mThreadPool.remove(runnable);
        if (runnable instanceof BackgroundTask){
            removeFromMain(runnable);
        }
    }

    /**
     * Blocks until all tasks have completed execution after a shutdown
     * request, or the timeout occurs, or the current thread is
     * interrupted, whichever happens first.
     *
     * @param timeout the maximum time to wait
     * @param unit the time unit of the timeout argument
     * @return {@code true} if this executor terminated and
     *         {@code false} if the timeout elapsed before termination
     * @throws InterruptedException if interrupted while waiting
     */
    public void awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        mThreadPool.awaitTermination(timeout,unit);
    }

    /**
     * 执行一个背景任务，执行完成后，在主线程执行 UI 操作。（与 AsyncTask 有点类似）
     * @param param     参数
     * @param runnable  Runnable
     */
    public synchronized <Param,Result> void postToWork(Param param,BackgroundTask<Param,Result> runnable){
        runnable.mParam = param;
        mThreadPool.execute(runnable);
    }
}
