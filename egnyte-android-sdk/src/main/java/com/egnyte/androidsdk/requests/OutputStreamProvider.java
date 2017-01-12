package com.egnyte.androidsdk.requests;

import java.io.OutputStream;

/**
 * Interface providing {@link OutputStream} for downloading
 */
public interface OutputStreamProvider {

    OutputStream provideOutputStream() throws Exception;
}
