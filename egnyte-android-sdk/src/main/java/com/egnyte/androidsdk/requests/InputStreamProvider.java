package com.egnyte.androidsdk.requests;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface providing {@link InputStream} for uploading
 */
public interface InputStreamProvider {

    InputStream provideInputStream() throws IOException;
}
