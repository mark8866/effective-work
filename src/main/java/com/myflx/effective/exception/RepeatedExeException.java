package com.myflx.effective.exception;

public class RepeatedExeException extends RuntimeException {
    public RepeatedExeException() {
    }

    public RepeatedExeException(String message) {
        super(message);
    }

    public RepeatedExeException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepeatedExeException(Throwable cause) {
        super(cause);
    }

    public RepeatedExeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
