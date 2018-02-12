package com.ronda.googleplay.manager;

import android.util.Log;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static android.R.attr.max;

/**
 * Created by Ronda on 2018/2/8.
 * <p>
 * 线程池
 */

public class ThreadPool {

    private static volatile ThreadPool mThreadPool;
    private ThreadPoolExecutor executor;

    private int corePoolSize;// 核心线程数
    private int maximumPoolSize;// 最大线程数
    private long keepAliveTime;// 多余的空闲的线程等待新任务的时间

    public static ThreadPool getThreadPool() {

        if (mThreadPool == null) {
            synchronized (ThreadPool.class) {
                if (mThreadPool == null) {
                    int processorCounts = Runtime.getRuntime().availableProcessors();// 处理器的数量/CPU的核数
                    Log.d("Liu", "processorCounts: " + processorCounts);

                    //int threadCount = processorCounts * 2 + 1;// 一般的写法. 但是如果核数过少的话, threadCount也会很少. 用户进行多个下载操作时就可能会出异常
                    int threadCount = 10;

                    mThreadPool = new ThreadPool(processorCounts, threadCount, 1L);
                }
            }
        }
        return mThreadPool;
    }

    private ThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.keepAliveTime = keepAliveTime;
    }

    public void execute(Runnable r) {
        /**
         * 这是java提供的一个线程池,用于并发操作
         * Creates a new {@code ThreadPoolExecutor} with the given initial
         * parameters.
         *
         * @param corePoolSize the number of threads to keep in the pool, even
         *        if they are idle(空闲的), unless {@code allowCoreThreadTimeOut} is set
         *        核心数, 线程池中至少要保持的线程的数量
         * @param maximumPoolSize the maximum number of threads to allow in the
         *        pool
         *        最大线程数量
         * @param keepAliveTime when the number of threads is greater than
         *        the core, this is the maximum time that excess(超过,多余的) idle threads
         *        will wait for new tasks before terminating(结束).
         *        当线程数大于核心数时，这是多余空闲线程在终止之前等待新任务的最长时间。
         * @param unit the time unit for the {@code keepAliveTime} argument
         *        keepAliveTimed的时间单位
         * @param workQueue the queue to use for holding tasks before they are
         *        executed.  This queue will hold only the {@code Runnable}
         *        tasks submitted by the {@code execute} method.
         *        用于在执行任务之前保存任务的队列。当调用execute()方法后,Runnable实际上是先进入队列等待,最后被执行
         * @param threadFactory the factory to use when the executor
         *        creates a new thread
         *        创建线程的工厂
         * @param handler the handler to use when execution is blocked
         *        because the thread bounds and queue capacities are reached
         *        线程异常处理策略
         * @throws IllegalArgumentException if one of the following holds:<br>
         *         {@code corePoolSize < 0}<br>
         *         {@code keepAliveTime < 0}<br>
         *         {@code maximumPoolSize <= 0}<br>
         *         {@code maximumPoolSize < corePoolSize}
         * @throws NullPointerException if {@code workQueue}
         *         or {@code threadFactory} or {@code handler} is null
         */
        if (executor == null) {
            executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        }

        // 线程池执行一个Runnable对象, 具体运行时机线程池ThreadPoolExecutor说了算
        executor.execute(r);
    }

    /**
     * 取消任务.
     * 注意: 只能移除还在队列中的任务.若是 Runnable 正在执行, 则是取消不了的.
     */
    public void cancel(Runnable r) {
        if (executor != null) {
            // 从线程队列中移除对象
            executor.getQueue().remove(r);
        }
    }

}

