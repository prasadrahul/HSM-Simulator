package com.hsm.simulator.exception;

public class CryptoException extends RuntimeException {
    public CryptoException(String message) {
        super(message);
    }
    public CryptoException(String message, Throwable cause) {
        super(message, cause);
    }

    public CryptoException(String message, String extraMessage) {
        super(String.format(message, extraMessage));
    }
}
