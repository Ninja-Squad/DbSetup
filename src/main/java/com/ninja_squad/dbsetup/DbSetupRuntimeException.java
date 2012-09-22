package com.ninja_squad.dbsetup;

/**
 * A runtime exception indicating that a DbSetup failed.
 * @author JB Nizet
 */
public class DbSetupRuntimeException extends RuntimeException {

    public DbSetupRuntimeException() {
        super();
    }

    public DbSetupRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public DbSetupRuntimeException(String message) {
        super(message);
    }

    public DbSetupRuntimeException(Throwable cause) {
        super(cause);
    }
}
