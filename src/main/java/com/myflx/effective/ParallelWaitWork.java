package com.myflx.effective;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 工作基础定义
 */
public class ParallelWaitWork extends AbstractParallelWork {

    public ParallelWaitWork(String name, IWorkProcessor... parallelWork) {
        super(name, parallelWork);
    }

    public ParallelWaitWork(IWorkProcessor... parallelWork) {
        super(parallelWork);
    }

    @Override
    public void processFinally(Exception processException, StopWatch stopWatch) {
        System.out.printf("\n\r任务:%s ,线程:%s ,开始:%s ,持续:%s",
                getName(), Thread.currentThread().getName(),
                getStartTimeMills(), getDuration());
    }
}

