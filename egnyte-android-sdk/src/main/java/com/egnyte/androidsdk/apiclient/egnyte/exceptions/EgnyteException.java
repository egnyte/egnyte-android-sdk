package com.egnyte.androidsdk.apiclient.egnyte.exceptions;

import java.io.IOException;

/**
 * This class represents error that occured during processing request
 */
public class EgnyteException extends IOException {

    public final static int DEFAULT_CODE = -1;

    private final int code;
    private final String apiExceptionMessage;

    public EgnyteException(int code, String message) {
        super(message);
        this.code = code;
        this.apiExceptionMessage = message;
    }

    public EgnyteException(Throwable thorowable) {
        super(thorowable);
        this.code = DEFAULT_CODE;
        apiExceptionMessage = thorowable.getMessage();
    }

    /**
     * Get descriptive error message.
     * @return
     */
    public String getApiExceptionMessage() {
        return apiExceptionMessage;
    }

    /**
     * HTTP error code if request failed because of HTTP error. {@link} #DEFAULT_CODE otherwise.
     * @return
     */
    public int getCode() {
        return code;
    }
}
