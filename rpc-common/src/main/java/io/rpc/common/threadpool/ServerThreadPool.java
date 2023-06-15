package io.rpc.common.threadpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 服务提供者一端执行异步任务
 */
public class ServerThreadPool {
    private static ThreadPoolExecutor threadPoolExecutor;

    static {
        threadPoolExecutor = new ThreadPoolExecutor(
                16,
                16,
                600L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(65536));
    }

    /**
     * 提交任务
     *
     * @param task
     */
    public static void submit(Runnable task) {
        threadPoolExecutor.submit(task);
    }

    /**
     * 关停线程池
     */
    public static void shutdown() {
        threadPoolExecutor.shutdown();
    }
}
