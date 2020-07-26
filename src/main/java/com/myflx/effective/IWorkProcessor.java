package com.myflx.effective;

/**
 * 工作处理器
 */
public interface IWorkProcessor {
    IWorkProcessor process();

    IWorkProcessor process(WorkProcessorChain processorChain);

    Boolean isProcessed();

    Integer parseExitCode();
}

