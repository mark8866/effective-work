package com.myflx.effective;

import org.springframework.util.StopWatch;

import java.util.List;
import java.util.Objects;

/**
 * 工作基础定义
 */
public class ConsistentParallelWork<T, V> extends AbstractParallelWork {

    @SafeVarargs
    public ConsistentParallelWork(String name, AbstractWork<T, V>... parallelWork) {
        super(name, parallelWork);
    }

    @SafeVarargs
    public ConsistentParallelWork(AbstractWork<T, V>... parallelWork) {
        super(parallelWork);
    }

    @Override
    public void processFinally(Exception processException, StopWatch stopWatch) {
        System.out.printf("\n\r任务:%s ,线程:%s ,开始:%s ,持续:%s",
                getName(), Thread.currentThread().getName(),
                getStartTimeMills(), getDuration());
    }

    public void params(T[] params) {
        int length = params.length;
        if (length < getParallelWork().size()) {
            for (int i = 0; i < length; i++) {
                getParallelWork().get(i).setInput(params[i]);
            }
        }

        if (length >= getParallelWork().size()) {
            for (int i = 0; i < getParallelWork().size(); i++) {
                getParallelWork().get(i).setInput(params[i]);
            }
        }
    }


    public void commonParams(T commonParam) {
        for (AbstractWork<T, V> single : getParallelWork()) {
            if (Objects.nonNull(single.getInput())) {
                continue;
            }
            single.setInput(commonParam);
        }
    }

    @Override
    public List<AbstractWork<T, V>> getParallelWork() {
        return (List<AbstractWork<T, V>>) super.getParallelWork();
    }
}

