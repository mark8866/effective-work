package com.myflx.effective;

import com.myflx.effective.exception.ExitException;
import com.myflx.effective.exception.MultiExitException;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 工作调度器
 * 1，调度任务
 * 2，掌握线程信息
 * 3，必要时发送告警信息
 */
public abstract class WorkResult {
    public static void assertExe(AbstractWork<?, ?>... works) {
        if (works == null || works.length <= 0) {
            throw new NullPointerException("target works not exits!");
        }
        List<ExitException> groupExceptions = new ArrayList<>();
        for (AbstractWork<?, ?> work : works) {
            ExitException exitException = work.getExitException();
            if (Objects.nonNull(exitException)) {
                groupExceptions.add(exitException);
            }
        }
        if (CollectionUtils.isNotEmpty(groupExceptions)) {
            throw new MultiExitException(groupExceptions);
        }
    }

    public static void assertExe(AbstractParallelWork parallelWork) {
        if (parallelWork == null || CollectionUtils.isEmpty(parallelWork.getParallelWork())) {
            throw new NullPointerException("target works not exits!");
        }
        List<? extends IWorkProcessor> works = parallelWork.getParallelWork();
        List<ExitException> groupExceptions = new ArrayList<>();
        for (IWorkProcessor work : works) {
            if (work instanceof AbstractWork) {
                ExitException exitException = ((AbstractWork<?, ?>) work).getExitException();
                if (Objects.nonNull(exitException)) {
                    groupExceptions.add(exitException);
                }
            } else if (work instanceof AbstractParallelWork) {
                assertExe((AbstractParallelWork) work);
            }
        }
        if (CollectionUtils.isNotEmpty(groupExceptions)) {
            throw new MultiExitException(groupExceptions);
        }
    }
}

