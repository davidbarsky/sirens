package com.davidbarsky.tree;

public class TreeException extends Exception {

    public TreeException() {
    }

    public TreeException(String message) {
        super(message);
    }

    public TreeException(String message, Throwable cause) {
        super(message, cause);
    }

    public TreeException(Throwable cause) {
        super(cause);
    }

    public TreeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
