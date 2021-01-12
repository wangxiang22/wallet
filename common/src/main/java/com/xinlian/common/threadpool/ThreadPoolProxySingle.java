package com.xinlian.common.threadpool;

public class ThreadPoolProxySingle {

    public static ThreadPoolProxy mThreadPoolProxy;

    /**
     * 创建单一的工作线程
     * @return
     */
    public static ThreadPoolProxy getThreadPoolProxy() {
        if (mThreadPoolProxy == null) {
            synchronized (ThreadPoolProxySingle.class) {
                if (mThreadPoolProxy == null) {
                    mThreadPoolProxy = new ThreadPoolProxy(1,1);
                }
            }
        }
        return mThreadPoolProxy;
    }
}
