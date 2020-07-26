package com.myflx.effective;

/**
 * 处理人负载，
 * 负载主体：工作的名称，入参，执行结果，执行异常
 */
public interface IPayload<T, V> {

    /**
     * 获取入参
     *
     * @return T
     */
    T getInput();

    /**
     * 获取执行结果
     *
     * @return V
     */
    V getOutput();

    /**
     * 返回处理结果：是否已经处理
     *
     * @return Boolean
     */
    Boolean isProcessed();

    /**
     * 执行异常
     *
     * @return exp
     */
    Exception getProcessException();

    /**
     * 返回处理时间 mills
     *
     * @return Long
     */
    Long getDuration();


    AbstractWork<T, V> reset(T t);

    AbstractWork<T, V> resetOrigin();
}
