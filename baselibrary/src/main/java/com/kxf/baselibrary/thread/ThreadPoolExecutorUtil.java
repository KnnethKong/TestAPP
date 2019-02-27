package com.kxf.baselibrary.thread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class ThreadPoolExecutorUtil {
    /**
     * ThreadPoolExecutor 执行 execute 时入队的规则如下：
     * 1. 如果线程数没达到 corePoolSize 指定的上限则创建新线程并执行，否则入队。
     * 2. 如果达到了队列上限则检查线程数是否达到了 maximumPoolSize 指定的上线，如果没有则创建新线程并执行。
     * 3. 如果队列与线程数均达到上限则执行失败走  RejectedExecutionHandler 处理器。
     *
     * 根据以上规则我们定制了 RejectedExecutionHandler 及 LinkedBlockingQueue 来达到下面的效果：
     * 1. 如果线程数没达到 corePoolSize 指定的上限则创建新线程并执行
     * 2. 如果线程数达到 corePoolSize 但没有达到 maximumPoolSize 指定的上限则创建新线程并执行
     * 3. 如果线程数达到 maximumPoolSize 指定的上限则将任务入队。
     * 4. 如果线程空闲并且线程数超过 corePoolSize 指定的上限则关闭空闲的线程
     *
     * 注意 keepAliveTime 必须大于 100 毫秒否则这个方案会有缺陷，会导致一些任务无法执行。
     *
     * @param corePoolSize      核心线程数
     * @param maximumPoolSize   最大线程数
     * @param keepAliveTime     空闲线程存活时间
     * @param unit              时间单柆
     * @return  ThreadPoolExecutor 实例
     */
    public static ThreadPoolExecutor newThreadPoolExecutor(int corePoolSize,
                                                           int maximumPoolSize,
                                                           long keepAliveTime,
                                                           TimeUnit unit) {
        return new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                new CustomLinkedBlockingQueue<Runnable>(),
                new DefaultThreadFactory(null),
                new RequestTaskRejectedExecutionHandler(null));
    }

    public static ThreadPoolExecutor newThreadPoolExecutor(int corePoolSize,
                                                           int maximumPoolSize,
                                                           long keepAliveTime,
                                                           TimeUnit unit,
                                                           String name) {
        return new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                new CustomLinkedBlockingQueue<Runnable>(),
                new DefaultThreadFactory(name),
                new RequestTaskRejectedExecutionHandler(name));
    }

    ///////////////////////////////////////////////////////////////////////////
    // 内部类
    ///////////////////////////////////////////////////////////////////////////
    private static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory(String name) {
            if (name == null){
                name = "pool";
            }

            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = name +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

    private static class RequestTaskRejectedExecutionHandler implements RejectedExecutionHandler {
        private String mThreadPoolName;

        RequestTaskRejectedExecutionHandler(String threadPoolName){
            mThreadPoolName = threadPoolName;
        }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            //ThreadPoolExecutor 线程数与队列均达到上线，时调用 _offer 将任务入队。
            ((CustomLinkedBlockingQueue)(executor.getQueue()))._offer(r);
        }
    }

    /**
     * 重写 offer 方法不充许入队，为的是造成队列满的情况，强制 ThreadPoolExecutor 创建新线程
     * 当 ThreadPoolExecutor 线程达到上线时则调用 _offer 方法来入队。
     * */
    private static class CustomLinkedBlockingQueue<T> extends LinkedBlockingQueue<T> {

        @Override
        public boolean offer(T t) {
            return false;
        }

        @Override
        public boolean offer(T t, long timeout, TimeUnit unit) throws InterruptedException {
            return false;
        }

        boolean _offer(T t){
            return super.offer(t);
        }
    }
}
