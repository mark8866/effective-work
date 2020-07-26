package com.myflx.effective;

/**
 * 工作能力接口，标记对象具有执行完成工作的能力
 * 泛型
 * T:入参
 * V:出参
 * 工作能力接口定义
 */
@FunctionalInterface
public interface IWork<T, V> {
    /**
     * 具体做的工作地点
     *
     * @param t 入参
     * @return 出参
     */
    V doWork(T t);
}
