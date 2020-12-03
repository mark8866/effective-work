package com.myflx.effective;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 工作等待，工作调度的执行单元
 */
public class WorkAwait {
    private final List<IWorkProcessor> workProcessors;

    public WorkAwait() {
        this.workProcessors = new ArrayList<>();
    }

    public static class Builder<T, V> {
        private final WorkAwait workAwait;
        private final ConsistentParallelWork<T, V> bootstrap;

        public Builder(ConsistentParallelWork<T, V> bootstrap) {
            this.bootstrap = bootstrap;
            this.workAwait = new WorkAwait();
        }

        @SafeVarargs
        public Builder(AbstractWork<T, V>... bootstrapWorks) {
            if (Objects.isNull(bootstrapWorks) || bootstrapWorks.length <= 0) {
                throw new IllegalArgumentException("初始任务不能为空");
            }
            this.bootstrap = buildDefaultWorkGroup(bootstrapWorks);
            this.workAwait = new WorkAwait();
        }

        @SafeVarargs
        public final Builder<T, V> startParams(T... params) {
            checkBootstrap();
            this.bootstrap.params(params);
            return this;
        }

        public Builder<T, V> commonStartParams(T commonParam) {
            checkBootstrap();
            this.bootstrap.commonParams(commonParam);
            return this;
        }

        /**
         * 任何依赖工作处理完成之后，依赖工作并行处理
         *
         * @param depends 依赖的工作
         * @param works   待处理工作
         * @param <E>     依赖工作出参，待处理工作入参
         * @return builder
         */
        public <E> Builder<T, V> anyDependDoneThenParalleProcess(AbstractWork<?, ?>[] depends,
            AbstractWork<?, ?>[] works) {
            ParallelWaitWork parallelWaitWork = new ParallelWaitWork(depends);
            for (AbstractWork<?, ?> work : depends) {
                work.addCallBackGroup(parallelWaitWork);
            }
            for (AbstractWork<?, ?> work : works) {
                parallelWaitWork.addAnyNextParallelWork(work);
            }
            return this;
        }

        /**
         * 所有依赖工作处理完成之后，依赖工作串行处理
         *
         * @param depends 依赖的工作
         * @param works   待处理工作
         * @param <E>     依赖工作出参，待处理工作入参
         * @return builder
         */
        public Builder<T, V> allProcessDependDoneThenParallel(AbstractWork<?, ?>[] depends, AbstractWork<?, ?>[] works) {
            ParallelWaitWork parallelWaitWork = new ParallelWaitWork(depends);
            for (AbstractWork<?, ?> work : depends) {
                work.addCallBackGroup(parallelWaitWork);
            }
            for (AbstractWork<?, ?> work : works) {
                parallelWaitWork.addNextParallelWork(work);
            }
            return this;
        }

        public Builder<T, V> allDependDoneThenSerialProcess(AbstractWork<?, ?>[] depends, AbstractWork<?, ?>[] works) {
            ParallelWaitWork parallelWaitWork = new ParallelWaitWork(depends);
            for (AbstractWork<?, ?> work : depends) {
                work.addCallBackGroup(parallelWaitWork);
            }
            for (AbstractWork<?, ?> work : works) {
                parallelWaitWork.addNextSerialWork(work);
            }
            return this;
        }

        /**
         * 运行依赖
         *
         * @param depend
         * @param work
         * @return
         */
        public Builder<T, V> processDependSerialProcess(AbstractWork<?, ?> depend, AbstractWork<?, ?> work) {
            depend.addNextSerialWork(work);
            return this;
        }

        /**
         * 运行依赖
         * 并发处理
         *
         * @param depend
         * @param work
         * @return
         */
        public Builder<T, V> processDependParallelProcess(AbstractWork<?, ?> depend, AbstractWork<?, ?> work) {
            depend.addNextParallelWork(work);
            return this;
        }

        /**
         * 运行结果依赖
         *
         * @param depend
         * @param work
         * @param <E>
         * @return
         */
        public <E> Builder<T, V> resultDependSerialProcess(AbstractWork<?, E> depend, AbstractWork<E, ?> work) {
            depend.addNextSerialWork(work);
            work.setInputSourceWork(depend);
            return this;
        }

        public <E> Builder<T, V> resultDependParallelProcess(AbstractWork<?, E> depend, AbstractWork<E, ?> work) {
            depend.addNextParallelWork(work);
            work.setInputSourceWork(depend);
            return this;
        }

        @SafeVarargs
        private final ConsistentParallelWork<T, V> buildDefaultWorkGroup(AbstractWork<T, V>... works) {
            return new ConsistentParallelWork<>(Arrays.stream(works).map(AbstractWork::getName).collect(Collectors.joining(",")), works);
        }

        public WorkAwait buildWait() {
            doBuildWorkWait();
            return workAwait;
        }

        /**
         * 执行构建操作
         */
        private void doBuildWorkWait() {
            workAwait.getWorkProcessors().add(bootstrap);
        }

        private void checkBootstrap() {
            if (Objects.isNull(bootstrap)) {
                throw new IllegalArgumentException("初始任务不能为空");
            }
        }
    }

    public List<IWorkProcessor> getWorkProcessors() {
        return workProcessors;
    }
}
