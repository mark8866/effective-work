package com.myflx.effective;

/**
 * 代理任务
 * 不想单独创建任务时候可以创建代理任务
 */
public class DelegateWork<T, V> extends AbstractWork<T, V> {
    private final IWork<T, V> delegate;

    public DelegateWork(IWork<T, V> work) {
        super("");
        this.delegate = work;
    }

    public DelegateWork(String name, IWork<T, V> work) {
        super(name);
        this.delegate = work;
    }

    public DelegateWork(String name, T input, IWork<T, V> work) {
        super(name, input);
        this.delegate = work;
    }

    @Override
    public V doWork(T t) {
        return delegate.doWork(t);
    }

    @Override
    public Integer parseExitCode() {
        if (getProcessException() != null) {
            return -1;
        }
        return null;
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
}

