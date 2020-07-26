package com.myflx.effective;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 工作基础定义
 */
public abstract class AbstractParallelWork implements IWorkProcessor, IWorkLifeCycle {
    protected static final String NAME_PATTERN = "ParallelWork[%s]";
    protected String name;
    protected List<? extends IWorkProcessor> parallelWork;
    private final AtomicInteger processPlus = new AtomicInteger(0);
    protected Boolean processed;
    protected volatile Long startTimeMills;
    protected volatile Long endTimeMills;
    protected volatile Long duration;
    protected List<AbstractWork<?, ?>> nextParallelWorks;
    protected List<AbstractWork<?, ?>> anyNextParallelWorks;
    protected volatile Boolean anyNextParallelWorkDone;
    protected List<AbstractWork<?, ?>> nextSerialWorks;
    private final List<AbstractParallelWork> callBackGroup;

    public AbstractParallelWork(String name, IWorkProcessor... parallelWork) {
        this.processed = false;
        this.anyNextParallelWorkDone = false;
        this.name = String.format(NAME_PATTERN, name);
        this.parallelWork = Arrays.asList(parallelWork);
        awareParallelWorkCallBackGroup(this.parallelWork);
        callBackGroup = new ArrayList<>(2);
        nextParallelWorks = new ArrayList<>(2);
        nextSerialWorks = new ArrayList<>(2);
        anyNextParallelWorks = new ArrayList<>(2);
    }

    public AbstractParallelWork(IWorkProcessor... parallelWorks) {
        this.processed = false;
        this.anyNextParallelWorkDone = false;
        this.name = String.format(NAME_PATTERN, "default");
        this.parallelWork = Arrays.asList(parallelWorks);
        awareParallelWorkCallBackGroup(this.parallelWork);
        callBackGroup = new ArrayList<>(2);
        nextParallelWorks = new ArrayList<>(2);
        nextSerialWorks = new ArrayList<>(2);
        anyNextParallelWorks = new ArrayList<>(2);
    }

    private void awareParallelWorkCallBackGroup(List<? extends IWorkProcessor> parallelWork) {
        for (IWorkProcessor workProcessor : parallelWork) {
            if (workProcessor instanceof AbstractWork) {
                ((AbstractWork<?, ?>) workProcessor).addCallBackGroup(this);
            }
            if (workProcessor instanceof AbstractParallelWork) {
                ((AbstractParallelWork) workProcessor).addCallBackGroup(this);
            }
        }
    }

    @Override
    public AbstractParallelWork process() {
        return process(new WorkProcessorChain(1000000L, WorkManager.COMMON_POOL));
    }

    @Override
    public AbstractParallelWork process(WorkProcessorChain processorChain) {
        try {
            this.startTimeMills = System.currentTimeMillis();
            List<? extends IWorkProcessor> workList = parallelWork;
            List<CompletableFuture<?>> futures = new ArrayList<>();
            for (IWorkProcessor work : workList) {
                CompletableFuture<?> future = CompletableFuture.runAsync(() -> work.process(processorChain), processorChain.getExecutor());
                futures.add(future);
            }
            whenAwareFutures(processorChain, futures);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.endTimeMills = System.currentTimeMillis();
            this.duration = endTimeMills - startTimeMills;
            processFinally(null, null);
        }
        return this;
    }

    protected void whenAwareFutures(WorkProcessorChain processorChain, List<CompletableFuture<?>> futures) throws Exception {
        if (!CollectionUtils.isEmpty(futures)) {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[0])).get(processorChain.getRemainTime().get(), TimeUnit.MILLISECONDS);
        }
    }

    protected void whenComplete(IWorkProcessor workProcessor) {
        if (workProcessor.isProcessed()) {
            if (!anyNextParallelWorkDone && !CollectionUtils.isEmpty(anyNextParallelWorks)){
                anyNextParallelWorkDone = true;
                IWorkProcessor[] workProcessor1 = new IWorkProcessor[anyNextParallelWorks.size()];
                for (int i = 0; i < anyNextParallelWorks.size(); i++) {
                    AbstractWork<?, ?> nextParallelWork = anyNextParallelWorks.get(i);
                    workProcessor1[i] = nextParallelWork;
                }
                new ParallelWaitWork(workProcessor1).process();
            }
            int incrementAndGet = processPlus.incrementAndGet();
            if (incrementAndGet == parallelWork.size()) {
                this.processed = true;
                if (org.apache.commons.collections.CollectionUtils.isNotEmpty(this.callBackGroup)) {
                    callBackGroup.forEach(group -> group.whenComplete(this));
                }
                if (org.apache.commons.collections.CollectionUtils.isNotEmpty(nextParallelWorks)) {
                    IWorkProcessor[] workProcessor1 = new IWorkProcessor[nextParallelWorks.size()];
                    for (int i = 0; i < nextParallelWorks.size(); i++) {
                        AbstractWork<?, ?> nextParallelWork = nextParallelWorks.get(i);
                        workProcessor1[i] = nextParallelWork;
                    }
                    new ParallelWaitWork(workProcessor1).process();
                }
                if (org.apache.commons.collections.CollectionUtils.isNotEmpty(nextSerialWorks)) {
                    AbstractWork<?,?>[] workProcessor1 = new AbstractWork[nextSerialWorks.size()];
                    for (int i = 0; i < nextSerialWorks.size(); i++) {
                        AbstractWork<?, ?> nextParallelWork = nextSerialWorks.get(i);
                        workProcessor1[i] = nextParallelWork;
                    }
                    WorkManager.processAll(workProcessor1);
                }
            }
        }
    }

    public void addCallBackGroup(AbstractParallelWork waitWork) {
        if (!callBackGroup.contains(waitWork)) {
            callBackGroup.add(waitWork);
        }
    }

    @Override
    public void whenProcessException(Exception processException, StopWatch stopWatch) {

    }

    @Override
    public Boolean isProcessed() {
        return processed;
    }

    @Override
    public Integer parseExitCode() {
        return null;
    }

    public String getName() {
        return name;
    }

    public List<? extends IWorkProcessor> getParallelWork() {
        return parallelWork;
    }

    /**
     * 并行任务重置执行状态，并设置参数为原来的参数
     *
     * @return obj
     */
    public AbstractParallelWork reset() {
        if (!CollectionUtils.isEmpty(getParallelWork())) {
            for (IWorkProcessor abstractWork : getParallelWork()) {
                if (abstractWork instanceof AbstractWork) {
                    ((AbstractWork<?, ?>) abstractWork).resetOrigin();
                }
                if (abstractWork instanceof AbstractParallelWork) {
                    ((AbstractParallelWork) abstractWork).reset();
                }
            }
        }
        return this;
    }

    public Long getStartTimeMills() {
        return startTimeMills;
    }

    public Long getEndTimeMills() {
        return endTimeMills;
    }

    public Long getDuration() {
        return duration;
    }

    public void addNextParallelWork(AbstractWork<?, ?> nextWork) {
        if (!nextParallelWorks.contains(nextWork)) {
            nextParallelWorks.add(nextWork);
        }
    }
    public void addAnyNextParallelWork(AbstractWork<?, ?> nextWork) {
        if (!anyNextParallelWorks.contains(nextWork)) {
            anyNextParallelWorks.add(nextWork);
        }
    }
    public void addNextSerialWork(AbstractWork<?, ?> nextWork) {
        if (!nextSerialWorks.contains(nextWork)) {
            nextSerialWorks.add(nextWork);
        }
    }
}

