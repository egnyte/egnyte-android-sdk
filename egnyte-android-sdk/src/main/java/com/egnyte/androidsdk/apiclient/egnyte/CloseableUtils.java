package com.egnyte.androidsdk.apiclient.egnyte;

import java.io.Closeable;
import java.io.IOException;

/**
 * Helper class for closing {@link Closeable}
 */
public class CloseableUtils {

    /**
     * Null-safely call {@link Closeable#close()} and ignore thrown {@link IOException}
     * @param closeable
     */
    public static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignore) {
            }
        }
    }
}
