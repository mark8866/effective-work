package com.myflx.effective;

import com.myflx.effective.exception.MultiExitException;
import com.myflx.effective.work.StuWork;
import com.myflx.effective.work.User;
import com.myflx.effective.work.UserService;
import org.junit.Test;

public class WorkLaunchTest {
    @Test
    public void testParallelLaunch() {
        DelegateWork<String, User> w1 = new DelegateWork<>("執行用戶任务1", "lsl", UserService::doWork);
        DelegateWork<String, User> w2 = new DelegateWork<>("執行用戶任务2", UserService::doWork);
        WorkAwait build = new WorkAwait.Builder<>(w1, w2)
            .startParams("luck", "lucas")
            .buildWait();
        WorkManager.launch(build);

        WorkManager.resetOrigin(w1, w2);
        WorkManager.launch(build);
        WorkResult.assertExe(w1, w2);
        System.out.println(WorkManager.getThreadCount());
    }


    @Test(expected = MultiExitException.class)
    public void testExeThrowGroupExitExp() {
        DelegateWork<String, User> w1 = new DelegateWork<>("執行用戶任务1", "lsl", UserService::doWork);
        DelegateWork<String, User> w2 = new DelegateWork<>("執行用戶任务2", UserService::doWork);
        WorkAwait build = new WorkAwait.Builder<>(w1, w2)
            .startParams("lucifer", "lucas")
            .buildWait();
        WorkManager.launch(build);
        WorkResult.assertExe(w1, w2);
        System.out.println(WorkManager.getThreadCount());
    }

    @Test
    public void testResultDependSerialWork() {
        DelegateWork<String, User> w1 = new DelegateWork<>("執行用戶任务1", "lsl", UserService::doWork);
        DelegateWork<String, User> w2 = new DelegateWork<>("執行用戶任务2", "luck", UserService::doWork);
        StuWork stuWork = new StuWork("执行学生用户相关用户工作");
        WorkAwait build = new WorkAwait.Builder<>(w1, w2)
            .resultDependSerialProcess(w1, new StuWork("执行学生用户相关用户工作1"))
            .resultDependSerialProcess(w1, new StuWork("执行学生用户相关用户工作2"))
            .buildWait();
        WorkManager.launch(build);
        WorkResult.assertExe(w1, w2, stuWork);
        System.out.println(WorkManager.getThreadCount());
    }

    @Test
    public void testResultDependParallelWork() {
        DelegateWork<String, User> w1 = new DelegateWork<>("執行用戶任务1", "lsl", new IWork<String, User>() {
            @Override
            public User doWork(String name) {
                return UserService.doWork(name);
            }
        });
        DelegateWork<String, User> w2 = new DelegateWork<>("執行用戶任务2", "luck", new IWork<String, User>() {
            @Override
            public User doWork(String name) {
                return UserService.doWork(name);
            }
        });
        StuWork stuWork = new StuWork("执行学生用户相关用户工作");
        WorkAwait build = new WorkAwait.Builder<>(w1, w2)
            .resultDependParallelProcess(w1, new StuWork("执行学生用户相关用户工作1"))
            .resultDependParallelProcess(w1, new StuWork("执行学生用户相关用户工作2"))
            .buildWait();
        WorkManager.launch(build);
        WorkResult.assertExe(w1, w2, stuWork);
        System.out.println(WorkManager.getThreadCount());
    }


    @Test
    public void testMultiProcessDependSerialProcess() {
        DelegateWork<String, User> w1 = new DelegateWork<>("執行用戶任务1", "lsl", UserService::doWork);
        DelegateWork<String, User> w2 = new DelegateWork<>("執行用戶任务2", "luck", UserService::doWork);

        StuWork stuWork = new StuWork("执行学生用户相关用户工作");
        stuWork.param(new User(2L, 20, "luoshanglin"));
        StuWork stuWork1 = new StuWork("执行学生用户相关用户工作1");
        stuWork1.param(new User(3L, 20, "luoshanglin2"));

        WorkAwait build = new WorkAwait.Builder<>(w1, w2)
            .processDependSerialProcess(w1, stuWork)
            .processDependSerialProcess(w1, stuWork1)
            .buildWait();
        WorkManager.launch(build);
        WorkResult.assertExe(w1, w2, stuWork, stuWork1);
        System.out.println(WorkManager.getThreadCount());
    }


    @Test
    public void testMultiProcessDependParallelProcess() {
        DelegateWork<String, User> w1 = new DelegateWork<>("執行用戶任务1", "lsl", UserService::doWork);
        DelegateWork<String, User> w2 = new DelegateWork<>("執行用戶任务2", "luck", UserService::doWork);

        StuWork stuWork = new StuWork("执行学生用户相关用户工作");
        stuWork.param(new User(2L, 20, "luoshanglin"));
        StuWork stuWork1 = new StuWork("执行学生用户相关用户工作1");
        stuWork1.param(new User(3L, 20, "luoshanglin2"));

        WorkAwait build = new WorkAwait.Builder<>(w1, w2)
            .processDependParallelProcess(w1, stuWork)
            .processDependParallelProcess(w1, stuWork1)
            .buildWait();
        WorkManager.launch(build);
        WorkResult.assertExe(w1, w2, stuWork, stuWork1);
        System.out.println(WorkManager.getThreadCount());
    }


    @Test(expected = MultiExitException.class)
    public void testMultiProcessDependParallelProcessStopWhenDependExp() {
        DelegateWork<String, User> w1 = new DelegateWork<>("執行用戶任务1", "lucifer", UserService::doWork);
        DelegateWork<String, User> w2 = new DelegateWork<>("執行用戶任务2", "luck", UserService::doWork);

        StuWork stuWork = new StuWork("执行学生用户相关用户工作");
        stuWork.param(new User(2L, 20, "luoshanglin"));
        StuWork stuWork1 = new StuWork("执行学生用户相关用户工作1");
        stuWork1.param(new User(3L, 20, "luoshanglin2"));

        WorkAwait build = new WorkAwait.Builder<>(w1, w2)
            .processDependParallelProcess(w1, stuWork)
            .processDependParallelProcess(w1, stuWork1)
            .buildWait();
        WorkManager.launch(build);
        WorkResult.assertExe(w1, w2, stuWork, stuWork1);
        System.out.println(WorkManager.getThreadCount());
    }

    @Test
    public void testAllDependDoneThenParallelProcess() {

        DelegateWork<String, User> w1 = new DelegateWork<>("執行用戶任务1", "Luca", UserService::doWork);
        DelegateWork<String, User> w2 = new DelegateWork<>("執行用戶任务2", "luck", UserService::doWork);

        StuWork stuWork = new StuWork("执行学生用户相关用户工作");
        stuWork.param(new User(2L, 20, "luoshanglin"));
        StuWork stuWork1 = new StuWork("执行学生用户相关用户工作1");
        stuWork1.param(new User(3L, 20, "luoshanglin2"));

        WorkAwait build = new WorkAwait.Builder<>(w1, w2)
            .commonStartParams("luo-shang-lin")
            .allProcessDependDoneThenParallel(new AbstractWork[] {w1, w2}, new AbstractWork[] {stuWork, stuWork1})
            .buildWait();
        WorkManager.launch(build);
        WorkResult.assertExe(w1, w2, stuWork, stuWork1);
        System.out.println(WorkManager.getThreadCount());
    }

    @Test
    public void testAnyDependDoneThenSerialProcess() {

        DelegateWork<String, User> w1 = new DelegateWork<>("執行用戶任务1", "Luca", UserService::doWork);
        DelegateWork<String, User> w2 = new DelegateWork<>("執行用戶任务2", "luck", UserService::doWork1);

        StuWork stuWork = new StuWork("执行学生用户相关用户工作");
        stuWork.param(new User(2L, 20, "luoshanglin"));
        StuWork stuWork1 = new StuWork("执行学生用户相关用户工作1");
        stuWork1.param(new User(3L, 20, "luoshanglin2"));

        WorkAwait build = new WorkAwait.Builder<>(w1, w2)
            .commonStartParams("luo-shang-lin")
            .anyDependDoneThenSerialProcess(new AbstractWork[] {w1, w2}, new AbstractWork[] {stuWork, stuWork1})
            .buildWait();
        WorkManager.launch(build);
        WorkResult.assertExe(w1, w2, stuWork, stuWork1);
        System.out.println(WorkManager.getThreadCount());
    }

    @Test
    public void testAllDependDoneThenSerialProcess() {

        DelegateWork<String, User> w1 = new DelegateWork<>("執行用戶任务1", "Luca", UserService::doWork);
        DelegateWork<String, User> w2 = new DelegateWork<>("執行用戶任务2", "luck", UserService::doWork1);

        StuWork stuWork = new StuWork("执行学生用户相关用户工作");
        stuWork.param(new User(2L, 20, "luoshanglin"));
        StuWork stuWork1 = new StuWork("执行学生用户相关用户工作1");
        stuWork1.param(new User(3L, 20, "luoshanglin2"));

        WorkAwait build = new WorkAwait.Builder<>(w1, w2)
            .commonStartParams("luo-shang-lin")
            .allDependDoneThenSerialProcess(new AbstractWork[] {w1, w2}, new AbstractWork[] {stuWork, stuWork1})
            .buildWait();
        WorkManager.launch(build);
        WorkResult.assertExe(w1, w2, stuWork, stuWork1);
        System.out.println(WorkManager.getThreadCount());
    }

}