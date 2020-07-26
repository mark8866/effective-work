package com.myflx.effective;

import com.myflx.effective.exception.MultiExitException;
import com.myflx.effective.work.UserService;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * 并行任务测试类
 */
public class WorkParallelProcessTest {
    @Test
    public void testManualProcess() {
        ParallelWaitWork parallelWork = new ParallelWaitWork(
                new DelegateWork<>("執行用戶任务1", "lsl", UserService::doWork),
                new DelegateWork<>("執行用戶任务2", "luck", UserService::doWork1));
        Assert.assertEquals(parallelWork, parallelWork.process());
        Assert.assertTrue(parallelWork.isProcessed());
    }

    @Test
    public void testManualProcessAsync() {
        ParallelAsyncWork parallelWork = new ParallelAsyncWork(
                new DelegateWork<>("執行用戶任务1", "lsl", UserService::doWork),
                new DelegateWork<>("執行用戶任务2", "luck", UserService::doWork1));
        Assert.assertEquals(parallelWork, parallelWork.process());
        Assert.assertFalse(parallelWork.isProcessed());
        WorkResult.assertExe(parallelWork);
    }

    @Test
    public void testManualProcessAsyncWait() throws InterruptedException {
        ParallelAsyncWork parallelWork = new ParallelAsyncWork(
                new DelegateWork<>("執行用戶任务1", "lsl", UserService::doWork),
                new DelegateWork<>("執行用戶任务2", "luck", UserService::doWork1));
        Assert.assertEquals(parallelWork, parallelWork.process());
        while (!parallelWork.isProcessed()){
            Thread.currentThread().sleep(100L);
        }
        WorkResult.assertExe(parallelWork);
    }

    @Test(expected = MultiExitException.class)
    public void testManualProcessThrow() {
        ParallelWaitWork parallelWork = new ParallelWaitWork(
                new DelegateWork<>("執行用戶任务1", "lucifer", UserService::doWork),
                new DelegateWork<>("執行用戶任务2", "luck", UserService::doWork1));
        Assert.assertEquals(parallelWork, parallelWork.process());
        Assert.assertTrue(parallelWork.isProcessed());
        WorkResult.assertExe(parallelWork);
    }

    @Test(expected = MultiExitException.class)
    public void testBatchManualProcessAllThrow() {
        AbstractWork<?, ?>[] abstractWorks = WorkManager.processAll(
                new DelegateWork<>("執行用戶任务1", "lucifer", UserService::doWork),
                new DelegateWork<>("執行用戶任务1", "lucifer1", UserService::doWork)
        );
        WorkResult.assertExe(abstractWorks);
    }

    @Test(expected = MultiExitException.class)
    public void testRepeatedProcessThrow() {
        ParallelWaitWork parallelWork = new ParallelWaitWork(
                new DelegateWork<>("執行用戶任务1", "luck1", UserService::doWork),
                new DelegateWork<>("執行用戶任务2", "luck2", UserService::doWork1));
        Assert.assertEquals(parallelWork, parallelWork.process().process());
        WorkResult.assertExe(parallelWork);
    }

    @Test
    public void testResetReProcess() {
        ParallelWaitWork parallelWork = new ParallelWaitWork(
                new DelegateWork<>("執行用戶任务1", "luck1", UserService::doWork),
                new DelegateWork<>("執行用戶任务2", "luck2", UserService::doWork1));
        Assert.assertEquals(parallelWork, parallelWork.process().reset().process().reset().process());
        Assert.assertTrue(parallelWork.isProcessed());
        WorkResult.assertExe(parallelWork);
    }

    @Test
    public void testMixedProcess() {
        ParallelWaitWork parallelWork = new ParallelWaitWork("1-12",
                new DelegateWork<>("執行用戶任务1", "lsl", UserService::doWork),
                new DelegateWork<>("執行用戶任务12", "luck", UserService::doWork1));

        ParallelWaitWork parallelWork2 = new ParallelWaitWork("1-12+21",
                parallelWork, new DelegateWork<>("執行用戶任务21", "luck2", UserService::doWork1));
        Assert.assertEquals(parallelWork2, parallelWork2.process());
        Assert.assertTrue(parallelWork2.isProcessed());
        WorkResult.assertExe(parallelWork);
    }
}