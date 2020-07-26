package com.myflx.effective.work;

import com.myflx.effective.exception.ExitException;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class UserService {
    public static User doWork(String name) {
        if (Objects.equals(name, "lucifer")) {
            throw new ExitException(-1, "暂无权限");
        }
        if (Objects.equals(name, "lucifer1")) {
            throw new IllegalStateException("暂无权限1");
        }
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

    public static User doWork1(String name) {
        if (Objects.equals(name, "lucifer")) {
            throw new ExitException(-1, "暂无权限");
        }
        if (Objects.equals(name, "lucifer1")) {
            throw new IllegalStateException("暂无权限1");
        }
        try {
            TimeUnit.SECONDS.sleep(2);
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

    public static User doWork2(Boolean lock) {
        try {
            System.out.println("函数化方法调用");
            TimeUnit.SECONDS.sleep(1);
            User user = new User();
            user.setId(1L);
            user.setAge(18);
            user.setName("name+" + lock.toString());
            return user;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Boolean doStuWork(User user) {
        boolean out = false;
        try {
            System.out.println();
            TimeUnit.SECONDS.sleep(2);
            out = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println();
        return out;
    }
}
