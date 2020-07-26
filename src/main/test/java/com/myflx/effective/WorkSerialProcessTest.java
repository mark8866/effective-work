package com.myflx.effective;

import com.myflx.effective.exception.MultiExitException;
import com.myflx.effective.work.User;
import com.myflx.effective.work.UserService;
import org.junit.Assert;
import org.junit.Test;

/**
 * 串行任务测试类
 */
public class WorkSerialProcessTest {
    @Test
    public void testManualProcess() {
        DelegateWork<String, User> w1 = new DelegateWork<>("執行用戶任务1", "lsl", UserService::doWork);
        Assert.assertEquals(w1, w1.process());
    }

    @Test
    public void testManualProcessAll() {
        AbstractWork<?, ?>[] abstractWorks = WorkManager.processAll(
                new DelegateWork<>("執行用戶任务1", "luck", UserService::doWork),
                new DelegateWork<>("執行用戶任务1", "lsl", UserService::doWork)
        );
        WorkResult.assertExe(abstractWorks);
    }


    @Test(expected = MultiExitException.class)
    public void testManualFailFastProcess() {
        AbstractWork<?, ?>[] abstractWorks = WorkManager.failFastProcess(
                new DelegateWork<>("執行用戶任务1", "lucifer", UserService::doWork),
                new DelegateWork<>("執行用戶任务1", "lsl", UserService::doWork)
        );
        Assert.assertFalse(abstractWorks[1].isProcessed());
        WorkResult.assertExe(abstractWorks);
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
    public void testRepeatedProcess() {
        AbstractWork<String, User> process = new DelegateWork<>("執行用戶任务1", "lsl", UserService::doWork).process();
        WorkResult.assertExe(process.process());
    }

    @Test
    public void testResetProcess() {
        DelegateWork<String, User> w1 = new DelegateWork<>("執行用戶任务1", "lsl", UserService::doWork);
        w1.process().reset("lsl1").process();
    }
}