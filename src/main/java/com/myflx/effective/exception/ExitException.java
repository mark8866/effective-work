package com.myflx.effective.exception;

public class ExitException extends RuntimeException {
    private Integer exitCode;

    public ExitException(Integer exitCode) {
        this.exitCode = exitCode;
    }

    public ExitException(Integer exitCode, String message) {
        super(message);
        this.exitCode = exitCode;
    }

    public ExitException(Integer exitCode, String message, Throwable cause) {
        super(message, cause);
        this.exitCode = exitCode;
    }

    public ExitException(Integer exitCode, Throwable cause) {
        super(cause);
        this.exitCode = exitCode;
    }

    public ExitException(Integer exitCode, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.exitCode = exitCode;
    }

    public Integer getExitCode() {
        return exitCode;
    }

    public void setExitCode(Integer exitCode) {
        this.exitCode = exitCode;
    }
}
