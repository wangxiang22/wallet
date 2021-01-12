package com.xinlian.common.threadpool;


import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @Description: 线程池代理
 */
@Slf4j
public class ThreadPoolProxy<T> {

    private static volatile List<Future<?>> futures = new ArrayList<>();

    private ThreadPoolExecutor mThreadPoolExecutor;
    private int mCorePoolSize;
    private int mMaximumPoolSize;

    public ThreadPoolProxy(int corePoolSize, int maximumPoolSize) {
        mCorePoolSize = corePoolSize;
        mMaximumPoolSize = maximumPoolSize;
    }

    /**
     * 初始化线程池
     */
    private void initThreadPoolExecutor() {
        log.info(Thread.currentThread().getName() + "=====Init_Thread_Pool====Ready");
        if (mThreadPoolExecutor == null || mThreadPoolExecutor.isShutdown() || mThreadPoolExecutor.isTerminated()) {
            synchronized (ThreadPoolProxy.class) {
                if (mThreadPoolExecutor == null || mThreadPoolExecutor.isShutdown() || mThreadPoolExecutor.isTerminated()) {
                    log.info(Thread.currentThread().getName() + "=====Init_Thread_Pool====Start");
                    futures.clear();
                    long keepAliveTime = 0;//这里不保持时间
                    TimeUnit unit = TimeUnit.MILLISECONDS;//毫秒
                    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();//无限队列
                    ThreadFactory threadFactory = Executors.defaultThreadFactory();//默认线程工厂

                    RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardPolicy();//这里不对异常进行处理
                    mThreadPoolExecutor = new ThreadPoolExecutor(
                            mCorePoolSize,//线程池核心数
                            mMaximumPoolSize,//最大线程数
                            keepAliveTime,//保持时间
                            unit,//时间单位
                            workQueue,//任务队列
                            threadFactory,//线程工厂
                            handler//异常捕获器
                    );

                    log.info(Thread.currentThread().getName() + "=====Init_Thread_Pool====End");
                }
            }
        }
    }

    /**
     * 提交任务 runnable
     *
     * @param task
     * @return 得到异步执行完成之后的结果
     */
    public Future<?> submitRunnable(Runnable task) {
        initThreadPoolExecutor();
        Future<?> future = mThreadPoolExecutor.submit(task);
        return future;
    }

    /**
     * 提交任务 Callable
     *
     * @param task
     * @return 得到异步执行完成之后的结果
     */
    public Future<?> submitCallable(Callable task) {
        initThreadPoolExecutor();
        Future<?> future = mThreadPoolExecutor.submit(task);
        addFutureResult(future);
        return future;
    }

    /**
     * 添加callable 返回结果
     *
     * @param future
     * @return
     */
    private void addFutureResult(Future<?> future) {
        futures.add(future);
    }

    /**
     * 获取线程池的返回结果
     *
     * @return
     */
    public List<Future<?>> getFutureResult() {
        return futures;
    }

    /**
     * 获取线程池的返回结果
     *
     * @return
     */
    public List<T> getFutureFormatResult() {
        List<T> reulstList = new ArrayList<>(futures.size());
        futures.forEach(future -> {
            try {
                T t = (T) future.get();
                reulstList.add(t);
            }catch (Exception e){
                log.error("转换异常{}",e.toString());
            }
        });
        return reulstList;
    }

    /**
     * 执行任务
     *
     * @param task
     */
    public void execute(Runnable task) {
        initThreadPoolExecutor();
        mThreadPoolExecutor.execute(task);
    }

    /**
     * 移除任务
     *
     * @param task
     */
    public void remove(Runnable task) {
        initThreadPoolExecutor();
        mThreadPoolExecutor.remove(task);
    }

    /**
     * 关闭线程池
     */
    public void shutdown() {
        log.info(Thread.currentThread().getName() + "_SHUTDOWN");
        mThreadPoolExecutor.shutdown();
    }
}
