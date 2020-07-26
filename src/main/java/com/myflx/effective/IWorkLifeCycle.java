package com.myflx.effective;

import org.springframework.util.StopWatch;

/**
 * 工作生命周期接口
 */
public interface IWorkLifeCycle {

    void whenProcessException(Exception processException, StopWatch stopWatch);

    void processFinally(Exception processException, StopWatch stopWatch);
}

