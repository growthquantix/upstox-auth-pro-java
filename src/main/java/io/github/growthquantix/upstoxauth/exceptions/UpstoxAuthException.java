package io.github.growthquantix.upstoxauth.exceptions;

public class UpstoxAuthException extends RuntimeException {
    public UpstoxAuthException(String message) {
        super(message);
    }

    public UpstoxAuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
