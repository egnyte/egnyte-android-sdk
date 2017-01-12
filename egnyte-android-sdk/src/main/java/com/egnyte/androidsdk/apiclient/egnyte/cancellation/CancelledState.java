package com.egnyte.androidsdk.apiclient.egnyte.cancellation;

import com.egnyte.androidsdk.apiclient.egnyte.exceptions.RequestCancelledException;

/**
 * This class represents if an ongoing request should be cancelled
 */
public class CancelledState {

    private boolean cancelled;

    /**
     * Set cancelled flag. This method is thread-safe.
     */
    public void setCancelled() {
        synchronized (this) {
            cancelled = true;
        }
    }

    /**
     * Gets cancelled flag. This method is thread-safe.
     * @return
     */
    public boolean isCancelled() {
        synchronized (this) {
            return cancelled;
        }
    }

    /**
     * Throws {@link RequestCancelledException} if {@link #isCancelled()} returns true
     * @throws RequestCancelledException
     */
    public void throwIfCancelled() throws RequestCancelledException {
        if (isCancelled()) {
            throw new RequestCancelledException();
        }
    }
}
