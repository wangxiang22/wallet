package com.xinlian.common.threadpool;

/**
 * @Description: 创建固定大小线程池代理
 * @Version 1.0
 */
public class ThreadPoolProxyFixed {

    public static ThreadPoolProxy mThreadPoolProxy;
    public static final int FIXED_THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    /**
     * 创建固定大小的线程池  类似于 newFixedThreadPool(5)
     * @return
     */
    public static ThreadPoolProxy getThreadPoolProxy() {
        if (mThreadPoolProxy == null) {
            synchronized (ThreadPoolProxyFixed.class) {
                if (mThreadPoolProxy == null) {
                    mThreadPoolProxy = new ThreadPoolProxy(FIXED_THREAD_POOL_SIZE, FIXED_THREAD_POOL_SIZE);
                }
            }
        }
        return mThreadPoolProxy;
    }
}
