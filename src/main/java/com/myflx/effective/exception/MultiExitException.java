package com.myflx.effective.exception;

import java.util.List;

public class MultiExitException extends RuntimeException {
    private List<ExitException> groupExceptions;

    public MultiExitException(List<ExitException> groupExceptions) {
        this.groupExceptions = groupExceptions;
    }

    public MultiExitException(String message, List<ExitException> groupExceptions) {
        super(message);
        this.groupExceptions = groupExceptions;
    }

    public MultiExitException(String message, Throwable cause, List<ExitException> groupExceptions) {
        super(message, cause);
        this.groupExceptions = groupExceptions;
    }

    public MultiExitException(Throwable cause, List<ExitException> groupExceptions) {
        super(cause);
        this.groupExceptions = groupExceptions;
    }

    public MultiExitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, List<ExitException> groupExceptions) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.groupExceptions = groupExceptions;
    }

    public List<ExitException> getGroupExceptions() {
        return groupExceptions;
    }
}
