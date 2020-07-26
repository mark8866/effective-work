package com.myflx.effective;

import org.springframework.util.StopWatch;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 工作基础定义
 */
public class ParallelAsyncWork extends AbstractParallelWork {

    public ParallelAsyncWork(String name, IWorkProcessor... parallelWork) {
        super(name, parallelWork);
    }

    public ParallelAsyncWork(IWorkProcessor... parallelWork) {
        super(parallelWork);
    }

    @Override
    protected void whenAwareFutures(WorkProcessorChain processorChain, List<CompletableFuture<?>> futures) {
        //ignore futures
        System.out.printf("\n\rignore futures:%s", futures);
    }

    @Override
    public void processFinally(Exception processException, StopWatch stopWatch) {
        System.out.printf("\n\r任务:%s ,线程:%s ,开始:%s ,持续:%s",
                getName(), Thread.currentThread().getName(),
                getStartTimeMills(), getDuration());
    }
}

