package com.egnyte.androidsdk.apiclient.egnyte.cancellation;

/**
 * This class represents that ongoing request cannot be cancelled
 */
public class LetItGo extends CancelledState {

    @Override
    public void setCancelled() {
        throw new IllegalStateException("LetItGo instance cannot be cancelled");
    }

    @Override
    public boolean isCancelled() {
        return false;
    }
}
