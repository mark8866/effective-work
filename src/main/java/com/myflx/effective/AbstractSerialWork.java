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
public abstract class AbstractSerialWork implements IWorkProcessor, IWorkLifeCycle {
    protected static final String NAME_PATTERN = "SerialWork[%s]";
    protected String name;
    protected List<? extends IWorkProcessor> serialWork;
    private final AtomicInteger processPlus = new AtomicInteger(0);
    protected Boolean processed;
    protected volatile Long startTimeMills;
    protected volatile Long endTimeMills;
    protected volatile Long duration;

    public AbstractSerialWork(String name, IWorkProcessor... serialWork) {
        this.processed = false;
        this.name = String.format(NAME_PATTERN, name);
        this.serialWork = Arrays.asList(serialWork);
    }

    public AbstractSerialWork(IWorkProcessor... serialWork) {
        this.processed = false;
        this.name = String.format(NAME_PATTERN, "default");
        this.serialWork = Arrays.asList(serialWork);
    }

    @Override
    public AbstractSerialWork process() {
        return process(new WorkProcessorChain(10000L, WorkManager.COMMON_POOL));
    }

    @Override
    public AbstractSerialWork process(WorkProcessorChain processorChain) {
        try {
            this.startTimeMills = System.currentTimeMillis();
            List<? extends IWorkProcessor> workList = serialWork;
            for (IWorkProcessor work : workList) {
                work.process(processorChain);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            processed = true;
            this.endTimeMills = System.currentTimeMillis();
            this.duration = endTimeMills - startTimeMills;
            processFinally(null, null);
        }
        return this;
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

    public List<? extends IWorkProcessor> getSerialWork() {
        return serialWork;
    }

    /**
     * 并行任务重置执行状态，并设置参数为原来的参数
     *
     * @return obj
     */
    public AbstractSerialWork reset() {
        if (!CollectionUtils.isEmpty(getSerialWork())) {
            for (IWorkProcessor abstractWork : getSerialWork()) {
                if (abstractWork instanceof AbstractWork) {
                    ((AbstractWork<?, ?>) abstractWork).resetOrigin();
                }
                if (abstractWork instanceof AbstractSerialWork) {
                    ((AbstractSerialWork) abstractWork).reset();
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
}

