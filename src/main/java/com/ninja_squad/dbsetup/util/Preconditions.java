package com.ninja_squad.dbsetup.util;

/**
 * Utility class to help verifying preconditions
 * @author JB
 */
public final class Preconditions {
    private Preconditions() {
    }

    /**
     * Throws a NullPointerException with the given message if the given argument is <code>null</code>.
     * @param argument the argument to check for <code>null</code>
     * @param message the message of the thrown NullPointerException
     * @throws NullPointerException if argument is <code>null</code>.
     */
    public static void checkNotNull(Object argument, String message) throws NullPointerException {
        if (argument == null) {
            throw new NullPointerException(message);
        }
    }

    /**
     * Throws an IllegalStateException with the given message if the given condition is <code>false</code>.
     * @param condition the condition to check
     * @param message the message of the thrown IllegalStateException
     * @throws IllegalStateException if the condition is <code>false</code>.
     */
    public static void checkState(boolean condition, String message) throws IllegalStateException {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    /**
     * Throws an IllegalARgumentException with the given message if the given condition is <code>false</code>.
     * @param condition the condition to check
     * @param message the message of the thrown IllegalArgumentException
     * @throws IllegalArgumentException if the condition is <code>false</code>.
     */
    public static void checkArgument(boolean condition, String message) throws IllegalStateException {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }
}
