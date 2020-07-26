package com.myflx.effective.work;

import com.myflx.effective.AbstractWork;

import java.util.concurrent.TimeUnit;

public class UserWork extends AbstractWork<String, User> {

    public UserWork(String name) {
        super(name);
        this.name = name;
    }

    @Override
    protected void workFinally(Exception processException) {

    }

    @Override
    protected void workOnException(Exception processException) {

    }


    @Override
    public User doWork(String name) {
        try {
            TimeUnit.SECONDS.sleep(1);
            User user = new User();
            user.setId(1L);
            user.setAge(18);
            user.setName(name);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
