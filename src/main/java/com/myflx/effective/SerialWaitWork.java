package com.myflx.effective;

import org.springframework.util.StopWatch;

/**
 * 工作基础定义
 */
public class SerialWaitWork<T, V> extends AbstractSerialWork {

    public SerialWaitWork(String name, AbstractWork<T, V>... serialWork) {
        super(name, serialWork);
    }

    public SerialWaitWork(AbstractWork<T, V>... serialWork) {
        super(serialWork);
    }

    @Override
    public void processFinally(Exception processException, StopWatch stopWatch) {
        System.out.printf("\n\r任务:%s ,线程:%s ,开始:%s ,持续:%s",
                getName(), Thread.currentThread().getName(),
                getStartTimeMills(), getDuration());
    }
}

