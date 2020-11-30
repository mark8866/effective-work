package com.myflx.effective;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 工作调度器
 * 1，调度任务
 * 2，掌握线程信息
 * 3，必要时发送告警信息
 */
public class WorkManager {
    public static final ThreadPoolExecutor COMMON_POOL =
        new ThreadPoolExecutor(10, 10,
            15L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            (ThreadFactory)Thread::new);

    private static final long DEFAULT_TIME_OUT = 10000000;

    public static void launch(WorkAwait await) {
        launch(await, DEFAULT_TIME_OUT);
    }

    public static void launch(WorkAwait await, long timeOut) {
        WorkProcessorChain processorChain = new WorkProcessorChain(timeOut, await, COMMON_POOL);
        processorChain.process(processorChain);
    }


    public static void shutDown() {
        COMMON_POOL.shutdown();
    }

    public static String getThreadCount() {
        return "\n\ractiveCount=" + COMMON_POOL.getActiveCount() +
                "  completedCount " + COMMON_POOL.getCompletedTaskCount() +
                "  largestCount " + COMMON_POOL.getLargestPoolSize();
    }

    public static AbstractWork<?, ?>[] processAll(AbstractWork<?, ?>... works) {
        if (works != null && works.length > 0) {
            for (AbstractWork<?, ?> work : works) {
                work.process();
            }
        }
        return works;
    }

    public static AbstractWork<?, ?>[] failFastProcess(AbstractWork<?, ?>... works) {
        if (works != null && works.length > 0) {
            for (AbstractWork<?, ?> work : works) {
                work.process();
                if (work.getExitException() != null){
                    break;
                }
            }
        }
        return works;
    }
    public static AbstractWork<?, ?>[] resetOrigin(AbstractWork<?, ?>... works) {
        if (works != null && works.length > 0) {
            for (AbstractWork<?, ?> work : works) {
                work.resetOrigin();
            }
        }
        return works;
    }
}

