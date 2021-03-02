package dev.lazurite.rayon.impl.util;

public class RayonException extends RuntimeException {
    public RayonException(String message) {
        super(message);
    }

    public RayonException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
