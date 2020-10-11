package me.nurio.microkernel.exceptions;

/**
 * This exception will be thrown when some loading module was invalid.
 */
public class InvalidModuleLoadException extends Exception {

    public InvalidModuleLoadException(String message) {
        super(message);
    }

}