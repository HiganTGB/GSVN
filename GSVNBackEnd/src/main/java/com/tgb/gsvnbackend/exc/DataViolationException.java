package com.tgb.gsvnbackend.exc;

import java.io.Serial;

public class DataViolationException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public DataViolationException() {
        super();
    }

    public DataViolationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataViolationException(String message) {
        super(message);
    }

    public DataViolationException(Throwable cause) {
        super(cause);
    }
}
