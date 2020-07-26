package com.myflx.effective;

import org.springframework.util.StopWatch;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 工作处理链
 */
@SuppressWarnings("unchecked")
public class WorkProcessorChain {
    private AtomicLong remainTime;
    private WorkAwait await;
    private ThreadPoolExecutor executor;
    private StopWatch stopWatch;

    public WorkProcessorChain(Long timeout, ThreadPoolExecutor executor) {
        this.remainTime = new AtomicLong(timeout);
        this.executor = executor;
    }

    public WorkProcessorChain(Long timeout, WorkAwait await, ThreadPoolExecutor executor) {
        this.remainTime = new AtomicLong(timeout);
        this.await = await;
        this.executor = executor;
    }

    public AtomicLong getRemainTime() {
        return remainTime;
    }

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }

    public void process(WorkProcessorChain processorChain) {
        try {
            List<IWorkProcessor> workProcessors = await.getWorkProcessors();
            for (IWorkProcessor workProcessor : workProcessors) {
                workProcessor.process(processorChain);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
