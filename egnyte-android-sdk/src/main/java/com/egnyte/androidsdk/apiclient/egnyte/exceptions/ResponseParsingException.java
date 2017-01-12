package com.egnyte.androidsdk.apiclient.egnyte.exceptions;

/**
 * Thrown when an error occured during parsing response
 */
public class ResponseParsingException extends Exception {
    public ResponseParsingException(Exception e) {
        super(e);
    }

    public ResponseParsingException(String message) {
        super(message);
    }
}
