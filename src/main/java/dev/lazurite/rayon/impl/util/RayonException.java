package dev.lazurite.rayon.impl.util;

/**
 * A generic exception that associates the error with Rayon.
 */
public class RayonException extends RuntimeException {
    public RayonException(String message) {
        super(message);
    }

    public RayonException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
