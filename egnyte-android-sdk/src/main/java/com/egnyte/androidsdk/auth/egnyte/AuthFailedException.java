package com.egnyte.androidsdk.auth.egnyte;

/**
 * This class represents error that happend during authentication flow
 */
public class AuthFailedException extends Exception {

    private final Type type;

    /**
     * Represents auth error type
     */
    public enum Type {
        USER_DENIED_ACCESS,
        INVALID_STATE,
        EXCHANGE_TOKEN_FAILED
    }

    public AuthFailedException(Type type) {
        this.type = type;
    }

    /**
     * Get error type
     * @return error type
     */
    public Type getType() {
        return type;
    }
}
