package com.myflx.effective.work;

import com.myflx.effective.AbstractWork;

import java.util.concurrent.TimeUnit;

public class StuWork extends AbstractWork<User, Boolean> {

    public StuWork(String name) {
        super(name);
        this.name = name;
    }

    @Override
    protected void workFinally(Exception processException) {
        System.out.printf("\n\r任务:%s ,线程:%s ,开始:%s ,持续:%s ,-入参【%s】，出参【%s】,执行异常【%s】",
                getName(), Thread.currentThread().getName(),
                getStartTimeMills(), getDuration(), getInput(), getOutput(), getProcessException());
    }

    @Override
    protected void workOnException(Exception processException) {

    }

    @Override
    public Boolean doWork(User user) {
        boolean out = false;
        try {
            TimeUnit.SECONDS.sleep(2);
            out = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }
}
