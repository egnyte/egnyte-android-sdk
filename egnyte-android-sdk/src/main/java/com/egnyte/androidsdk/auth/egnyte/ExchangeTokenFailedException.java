package com.egnyte.androidsdk.auth.egnyte;

/**
 * Represents error during exchanging code for a token
 */
public class ExchangeTokenFailedException extends AuthFailedException {

    private final Exception originalException;

    public ExchangeTokenFailedException(Exception originalException) {
        super(AuthFailedException.Type.EXCHANGE_TOKEN_FAILED);
        this.originalException = originalException;
    }

    /**
     * Get exception that caused exchanging code for a token to fail
     * @return
     */
    public Exception getOriginalException() {
        return originalException;
    }
}
