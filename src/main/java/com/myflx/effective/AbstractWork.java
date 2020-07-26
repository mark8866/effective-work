package com.myflx.effective;

import com.myflx.effective.exception.ExitException;
import com.myflx.effective.exception.RepeatedExeException;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 工作基础定义
 */
public abstract class AbstractWork<T, V> implements IWork<T, V>, IPayload<T, V>, IWorkLifeCycle, IWorkProcessor {
    protected String name;
    private volatile T input;
    private volatile V output;
    private volatile boolean processed;
    private volatile Exception processException;
    private volatile ExitException exitException;
    private volatile Long startTimeMills;
    private volatile Long endTimeMills;
    private volatile Long duration;
    protected AbstractWork<?, T> inputSourceWork;
    protected List<IWorkProcessor> nextProcessors;
    protected List<AbstractWork<?, ?>> nextParallelWorks;
    protected List<AbstractWork<?, ?>> nextSerialWorks;
    private final List<AbstractParallelWork> callBackGroup;

    public AbstractWork(String name) {
        nextParallelWorks = new ArrayList<>(2);
        nextSerialWorks = new ArrayList<>(2);
        callBackGroup = new ArrayList<>(2);
        this.processed = false;
        this.name = name;
    }

    public AbstractWork(String name, T input) {
        nextParallelWorks = new ArrayList<>(2);
        nextSerialWorks = new ArrayList<>(2);
        callBackGroup = new ArrayList<>(2);
        this.processed = false;
        this.name = name;
        this.input = input;
    }

    @Override
    public AbstractWork<T, V> process() {
        process(null);
        return this;
    }


    @Override
    public AbstractWork<T, V> process(WorkProcessorChain processorChain) {
        doProcess(this, processorChain, new StopWatch(getName()));
        return this;
    }

    /**
     * 实际处理实现
     *
     * @param work           工作
     * @param processorChain 处理链
     * @param stopWatch      监听器
     */
    private void doProcess(AbstractWork<T, V> work, WorkProcessorChain processorChain, StopWatch stopWatch) {
        try {
            if (isProcessed()) {
                throw new RepeatedExeException("任务已执行");
            }
            stopWatch.start();
            this.startTimeMills = System.currentTimeMillis();
            if (Objects.nonNull(processorChain) && processorChain.getRemainTime().get() <= 0) {
                System.err.println(work.getName() + "：执行超时");
                return;
            }
            if (Objects.nonNull(inputSourceWork)) {
                if (!inputSourceWork.processed) {
                    throw new IllegalStateException("依赖任务未执行");
                } else {
                    work.setInput(inputSourceWork.getOutput());
                }
            }
            work.setOutput(work.doWork(work.getInput()));
            if (stopWatch.isRunning()) {
                stopWatch.stop();
            }
        } catch (Exception e) {
            whenProcessException(e, stopWatch);
        } finally {
            processFinally(work.getProcessException(), stopWatch);
        }
        if (CollectionUtils.isNotEmpty(this.nextProcessors)) {
            for (IWorkProcessor nextSerialProcessor : this.nextProcessors) {
                nextSerialProcessor.process(processorChain);
            }
        }
    }

    @Override
    public Boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    /**
     * 获取工作名称
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    public T getInput() {
        return input;
    }

    public void setInput(T input) {
        this.input = input;
    }

    public AbstractWork<T, V> param(T input) {
        this.input = input;
        return this;
    }

    public V getOutput() {
        return output;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public void setOutput(V output) {
        this.output = output;
    }

    public Exception getProcessException() {
        return processException;
    }

    public void setProcessException(Exception processException) {
        this.processException = processException;
    }

    public ExitException getExitException() {
        return exitException;
    }

    public void setExitException(ExitException exitException) {
        this.exitException = exitException;
    }

    public Long getStartTimeMills() {
        return startTimeMills;
    }

    public Long getEndTimeMills() {
        return endTimeMills;
    }

    @Override
    public AbstractWork<T, V> resetOrigin() {
        return toReset(input);
    }

    @Override
    public AbstractWork<T, V> reset(T t) {
        return toReset(t);
    }

    public AbstractWork<T, V> toReset(T t) {
        this.input = t;
        this.output = null;
        this.processed = false;
        this.processException = null;
        this.exitException = null;
        this.startTimeMills = null;
        this.endTimeMills = null;
        return this;
    }

    @Override
    public Integer parseExitCode() {
        return null;
    }

    private void whenComplete() {
        if (CollectionUtils.isNotEmpty(this.callBackGroup)) {
            for (AbstractParallelWork abstractParallelWork : callBackGroup) {
                abstractParallelWork.whenComplete(this);
            }
        }
    }

    @Override
    public final void whenProcessException(Exception processException, StopWatch stopWatch) {
        this.setProcessException(processException);
        workOnException(processException);
        if (stopWatch.isRunning()) {
            stopWatch.stop();
        }
    }

    @Override
    public final void processFinally(Exception processException, StopWatch stopWatch) {
        ExitException exitException = null;
        if (processException instanceof ExitException) {
            exitException = (ExitException) processException;
        } else if (processException != null) {
            exitException = new ExitException(parseExitCode(), processException);
        }
        if (stopWatch.isRunning()) {
            stopWatch.stop();
        }
        setProcessed(true);
        setExitException(exitException);
        setDuration(System.currentTimeMillis() - getStartTimeMills());
        try {
            workFinally(processException);
            whenComplete();
            if (Objects.isNull(this.exitException) && CollectionUtils.isNotEmpty(nextParallelWorks)) {
                IWorkProcessor[] workProcessor = new IWorkProcessor[nextParallelWorks.size()];
                for (int i = 0; i < nextParallelWorks.size(); i++) {
                    AbstractWork<?, ?> nextParallelWork = nextParallelWorks.get(i);
                    workProcessor[i] = nextParallelWork;
                }
                new ParallelWaitWork(workProcessor).process();
            }
            if (Objects.isNull(this.exitException) && CollectionUtils.isNotEmpty(nextSerialWorks)) {
                WorkManager.processAll(nextSerialWorks.toArray(new AbstractWork[0]));
            }
        } catch (Exception e) {
            // ignore on exp
        }


    }

    public void addNextParallelWork(AbstractWork<?, ?> nextWork) {
        if (!nextParallelWorks.contains(nextWork)) {
            nextParallelWorks.add(nextWork);
        }
    }

    public void addNextSerialWork(AbstractWork<?, ?> nextWork) {
        if (!nextSerialWorks.contains(nextWork)) {
            nextSerialWorks.add(nextWork);
        }
    }

    public void setInputSourceWork(AbstractWork<?, T> inputSourceWork) {
        this.inputSourceWork = inputSourceWork;
    }

    public void addCallBackGroup(AbstractParallelWork waitWork) {
        if (!callBackGroup.contains(waitWork)) {
            callBackGroup.add(waitWork);
        }
    }

    protected abstract void workFinally(Exception processException);

    protected abstract void workOnException(Exception processException);
}

